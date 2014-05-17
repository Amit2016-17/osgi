import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.jar.Manifest;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.version.Version;
import aQute.libg.generics.Create;

/**
 * This script runs after the bnd file stuff has been done, before analyzing any
 * classes. It will check if the bnd file contains -pack (the bnd file must
 * contain it, not a parent). It will then pack all projects listed as its
 * valued. For each project, a bnd file is created that has no longer references
 * to the build. All dependent JAR files are stored in the jar directory for
 * this purpose. Additionally, a runtests script is added and the bnd jar is
 * included to make the tess self contained.
 */

public class Packaging implements AnalyzerPlugin {

	private final static String	PACK	= "-pack";

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		if (!(analyzer instanceof ProjectBuilder))
			return false;

		// Make sure -pack is set in the actual file or one of its includes
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

		for (String entry : params.keySet()) {
			try {
				Project project = workspace.getProject(entry);
				if (project != null) {
					pack(analyzer, jar, project, null, fileToPath);
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

		/**
		 * Add all sub bundles to the -runbundles so they are installed We
		 * assume here that the project is build ahead of time.
		 */
		File[] files = project.getBuildFiles();
		if (files == null) {
			System.out.println("Project has no build files " + project);
			return;
		}

		flatten(analyzer, null, jar, project,
				Collections.<String, String> emptyMap(), true, fileToPath);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Collection<Container> path, boolean store,
			Map<String, String> fileToPath) throws Exception {
		for (Container container : path) {
			flatten(analyzer, sb, jar, container, store, fileToPath);
		}
		if (sb != null)
			sb.deleteCharAt(sb.length() - 2);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Container container, boolean store, Map<String, String> fileToPath)
			throws Exception {
		switch (container.getType()) {
			case LIBRARY :
				flatten(analyzer, sb, jar, container.getMembers(), store,
						fileToPath);
				return;

			case PROJECT :
				flatten(analyzer, sb, jar, container.getProject(),
						container.getAttributes(), store, fileToPath);
				break;

			case EXTERNAL :
				flatten(analyzer, sb, jar, container.getFile(),
						container.getAttributes(), store, fileToPath);
				break;

			case REPO :
				flatten(analyzer, sb, jar, container.getFile(),
						container.getAttributes(), store, fileToPath);
				break;
			default :
				analyzer.error("Unrecognized container type: %s",
						container.getType());
				break;
		}
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			Project project, Map<String, String> map, boolean store,
			Map<String, String> fileToPath) throws Exception {
		File[] subs = project.getBuildFiles();
		analyzer.getInfo(project);
		if (subs == null) {
			analyzer.error("Project cannot build %s ", project);
		}
		else
			for (File sub : subs)
				flatten(analyzer, sb, jar, sub, map, store, fileToPath);
	}

	protected void flatten(Analyzer analyzer, StringBuilder sb, Jar jar,
			File sub, Map<String, String> map, boolean store,
			Map<String, String> fileToPath) throws Exception {
		String path = fileToPath.get(sub.getAbsolutePath());
		if (path == null) {
			path = "jar/" + canonicalName(analyzer, sub);
			fileToPath.put(sub.getAbsolutePath(), path);
		}
		if (store && (jar.getResource(path) == null)) {
			jar.putResource(path, new FileResource(sub));
		}
		if (sb != null) {
			sb.append("\\\n    ");
			sb.append(path);
			sb.append(";version=file");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (!entry.getKey().equals("version")) {
					sb.append(";");
					sb.append(entry.getKey());
					sb.append("=\"");
					sb.append(entry.getValue());
					sb.append("\"");
				}
			}
			sb.append(", ");
		}
	}

	protected String canonicalName(Analyzer analyzer, File sub)
			throws Exception {
		Jar s = new Jar(sub);
		try {
			Manifest m = s.getManifest();
			String bsn = m.getMainAttributes().getValue(
					Constants.BUNDLE_SYMBOLICNAME);
			if (bsn == null) {
				analyzer.error(
						"Invalid bundle in flattening a path (no Bundle-SymbolicName set): %s",
						sub.getAbsolutePath());
				return sub.getName();
			}

			int n = bsn.indexOf(';');
			if (n > 0)
				bsn = bsn.substring(0, n);
			bsn = bsn.trim();

			String version = m.getMainAttributes().getValue(
					Constants.BUNDLE_VERSION);
			Version v = Version.parseVersion(version);

			String path = bsn + "-" + v.getMajor() + "."
					+ v.getMinor() + "." + v.getMicro() + ".jar";
			return path;
		}
		finally {
			s.close();
		}
	}
}
