/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.print;
public interface DocPrintJob {
	void addPrintJobAttributeListener(javax.print.event.PrintJobAttributeListener var0, javax.print.attribute.PrintJobAttributeSet var1);
	void addPrintJobListener(javax.print.event.PrintJobListener var0);
	javax.print.attribute.PrintJobAttributeSet getAttributes();
	javax.print.PrintService getPrintService();
	void print(javax.print.Doc var0, javax.print.attribute.PrintRequestAttributeSet var1) throws javax.print.PrintException;
	void removePrintJobAttributeListener(javax.print.event.PrintJobAttributeListener var0);
	void removePrintJobListener(javax.print.event.PrintJobListener var0);
}

