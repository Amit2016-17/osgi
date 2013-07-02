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

package java.awt.image;
public interface ImageConsumer {
	public final static int COMPLETESCANLINES = 4;
	public final static int IMAGEABORTED = 4;
	public final static int IMAGEERROR = 1;
	public final static int RANDOMPIXELORDER = 1;
	public final static int SINGLEFRAME = 16;
	public final static int SINGLEFRAMEDONE = 2;
	public final static int SINGLEPASS = 8;
	public final static int STATICIMAGEDONE = 3;
	public final static int TOPDOWNLEFTRIGHT = 2;
	void imageComplete(int var0);
	void setColorModel(java.awt.image.ColorModel var0);
	void setDimensions(int var0, int var1);
	void setHints(int var0);
	void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, byte[] var5, int var6, int var7);
	void setPixels(int var0, int var1, int var2, int var3, java.awt.image.ColorModel var4, int[] var5, int var6, int var7);
	void setProperties(java.util.Hashtable<?,?> var0);
}

