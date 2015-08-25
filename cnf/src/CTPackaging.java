import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.service.Strategy;
import aQute.libg.generics.Create;

/**
 * This script runs after the bnd file stuff has been done, before analyzing any
 * classes. It will check if the bnd file contains -ctpack (the bnd file must
 * contain it, not a parent). It will then pack all projects listed as its
 * valued. For each project, a bnd file is created that has no longer references
 * to the build. All dependent JAR files are stored in the jar directory for
 * this purpose. Additionally, a runtests script is added and the bnd jar is
 * included to make the tess self contained.
 */

public class CTPackaging extends Packaging implements AnalyzerPlugin {

	private final static String	PACK	= "-ctpack";
	private final static String	TESTER	= "-tester";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		// Make sure -ctpack is set in the actual file or one of its includes
		if (!analyzer.getProperties().containsKey(PACK))
			return false;

		Map<String, String> fileToPath = Create.map();

		String pack = analyzer.getProperty(PACK);
		ProjectBuilder pb = (ProjectBuilder) analyzer;
		Workspace workspace = pb.getProject().getWorkspace();
		Jar jar = analyzer.getJar();

		// For each param listed ...
		Parameters params = pb.parseHeader(pack);
		if (params.isEmpty()) {
			analyzer.warning("No items to pack");
			return false;
		}

		// Do the shared stuff, we use our project as a template
		Project us = pb.getProject();
		Collection<Container> runpath = us.getRunpath();

		StringBuilder sb = new StringBuilder();
		addNotice(sb);
		sb.append("# Workspace information\n");
		sb.append(Constants.RUNPATH);
		sb.append(" = ");
		flatten(analyzer, sb, jar, runpath, false, fileToPath);
		sb.append("\n\n-runtrace = true\n\n");
		String tester = analyzer.getProperty(TESTER);
		if (tester != null) {
			sb.append(TESTER);
			sb.append(" = ");
			sb.append(tester);
			sb.append("\n\n");
		}

		jar.putResource("shared.inc", new EmbeddedResource(sb.toString()
				.getBytes("UTF-8"), 0));

		for (String entry : params.keySet()) {
			try {
				Project project = workspace.getProject(entry);
				if (project != null) {
					pack(analyzer, jar, project, runpath, fileToPath);
				}
				else {
					while (entry.endsWith("~")) {
						entry = entry.substring(0, entry.length() - 1);
					}
					flatten(analyzer, null, jar, new File(entry),
							Collections.<String, String> emptyMap(), true,
							fileToPath);
				}
			}
			catch (Exception t) {
				analyzer.error("While packaging %s got %s", entry, t);
				throw t;
			}
		}

		// Include bnd so it is fully self contained, except for the
		// java runtime.
		Container c = pb.getProject().getBundle("biz.aQute.bnd", "latest",
				Strategy.HIGHEST, null);

		File f = c.getFile();
		if (f != null)
			jar.putResource("jar/bnd.jar", new FileResource(f));
		else
			analyzer.error("Cannot find bnd's jar file in a repository ");

		StringBuilder script = new StringBuilder();
		script.append("java -jar jar/bnd.jar runtests --title ");
		script.append(pb.getProject());
		script.append("\n");
		jar.putResource("runtests", new EmbeddedResource(script.toString()
				.getBytes("UTF-8"), 0));

		return false;
	}

	/**
	 * Store a project in a JAR so that we can later unzip this project and have
	 * all information.
	 *
	 * @param jar
	 * @param project
	 * @throws Exception
	 */
	protected void pack(Analyzer analyzer, Jar jar, Project project,
			Collection<Container> sharedRunpath, Map<String, String> fileToPath)
			throws Exception {
		Collection<Container> runpath = project.getRunpath();
		Collection<Container> runbundles = project.getRunbundles();
		String runproperties = project.getProperty(Constants.RUNPROPERTIES);
		String runsystempackages = project
				.getProperty(Constants.RUNSYSTEMPACKAGES);
		String runframework = project.getProperty(Constants.RUNFRAMEWORK);
		String runvm = project.getProperty(Constants.RUNVM);
		StringBuilder sb = new StringBuilder();
		addNotice(sb);

		/**
		 * Add all sub bundles to the -runbundles so they are installed We
		 * assume here that the project is build ahead of time.
		 */
		File[] files = project.getBuildFiles();
		if (files == null) {
			System.out.println("Project has no build files " + project);
			return;
		}
		for (File sub : files) {
			Container c = new Container(project, sub);
			runbundles.add(c);
		}

		sb.append("# bnd pack for project " + project + "\n");
		sb.append("# ").append(new Date()).append("\n");
		sb.append("-include= ~shared.inc\n");
		sb.append("\n");
		sb.append("-target = ");
		flatten(analyzer, sb, jar, project,
				Collections.<String, String> emptyMap(), true, fileToPath);
		sb.deleteCharAt(sb.length() - 1);

		if (!equals(runpath, sharedRunpath)) {
			sb.append("\n");
			sb.append("\n");
			sb.append(Constants.RUNPATH);
			sb.append(" = ");
			flatten(analyzer, sb, jar, runpath, false, fileToPath);
		}
		sb.append("\n\n");
		sb.append(Constants.RUNBUNDLES);
		sb.append(" = ");
		flatten(analyzer, sb, jar, runbundles, false, fileToPath);

		Map<String, String> properties = OSGiHeader
				.parseProperties(runproperties);

		String del = "\n\n" + Constants.RUNPROPERTIES + " = \\\n    ";
		properties.put("report", "true");

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			sb.append(del);
			del = ", \\\n    ";

			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key);
			sb.append("=");

			value = replacePaths(analyzer, jar, fileToPath, value,
					key.endsWith(".bundles") == false);

			sb.append("\"");
			sb.append(value);
			sb.append("\"");
		}

		if (runsystempackages != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNSYSTEMPACKAGES);
			sb.append(" = \\\n    ");
			sb.append(runsystempackages);
		}

		if (runframework != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNFRAMEWORK);
			sb.append(" = \\\n    ");
			sb.append(runframework);
		}

		if (runvm != null) {
			sb.append("\n\n");
			sb.append(Constants.RUNVM);
			sb.append(" = \\\n    ");
			sb.append(runvm);
		}

		sb.append("\n\n\n\n");

		Resource r = new EmbeddedResource(sb.toString().getBytes("UTF-8"),
				project.lastModified());
		jar.putResource(project.getName() + ".bnd", r);

	}

	private String replacePaths(Analyzer analyzer, Jar jar,
			Map<String, String> fileToPath, String value, boolean store)
			throws Exception {
		Collection<String> paths = Processor.split(value);
		List<String> result = Create.list();
		for (String path : paths) {
			File f = analyzer.getFile(path);
			if (f.isAbsolute() && f.exists()
					&& !f.getPath().contains(analyzer.getProperty("target"))) {
				f = f.getCanonicalFile();
				path = fileToPath.get(f.getAbsolutePath());
				if (path == null) {
					if (f.getName().endsWith(".jar")) {
						path = "jar/" + canonicalName(analyzer, f);
						fileToPath.put(f.getAbsolutePath(), path);
					}
					else {
						path = "property-resources/" + f.getName();

						// Ensure names are unique
						int n = 1;
						while (jar.getResource(path) != null)
							path = "property-resources/" + f.getName() + "-"
									+ n++;

						fileToPath.put(f.getAbsolutePath(), path);
					}
				}
				if (store && (jar.getResource(path) == null)) {
					if (f.isFile()) {
						jar.putResource(path, new FileResource(f));
					}
					else {
						Jar j = new Jar(f);
						try {
							jar.addAll(j, null, path);
						}
						finally {
							j.close();
						}
					}
				}
				result.add(path);
			}
			else
				// If one entry is not a file not match, we assume they're not
				// paths
				return value;
		}
		return Processor.join(result);
	}

	private <T> boolean equals(Collection< ? extends T> a,
			Collection< ? extends T> b) {
		if (a.size() != b.size())
			return false;

		for (T x : a) {
			if (!b.contains(x))
				return false;
		}
		return true;
	}

	private void addNotice(StringBuilder sb) {
		sb.append("# Copyright (c) OSGi Alliance (")
				.append(Calendar.getInstance().get(Calendar.YEAR))
				.append("). All Rights Reserved.\n");
		sb.append("#\n");
		sb.append("# Licensed under the Apache License, Version 2.0 (the \"License\");\n");
		sb.append("# you may not use this file except in compliance with the License.\n");
		sb.append("# You may obtain a copy of the License at\n");
		sb.append("#\n");
		sb.append("#      http://www.apache.org/licenses/LICENSE-2.0\n");
		sb.append("#\n");
		sb.append("# Unless required by applicable law or agreed to in writing, software\n");
		sb.append("# distributed under the License is distributed on an \"AS IS\" BASIS,\n");
		sb.append("# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
		sb.append("# See the License for the specific language governing permissions and\n");
		sb.append("# limitations under the License.\n");
		sb.append("\n");
	}
}
