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

package java.awt.peer;
public interface ComponentPeer {
	public final static int DEFAULT_OPERATION = 3;
	public final static int NO_EMBEDDED_CHECK = 16384;
	public final static int RESET_OPERATION = 5;
	public final static int SET_BOUNDS = 3;
	public final static int SET_CLIENT_SIZE = 4;
	public final static int SET_LOCATION = 1;
	public final static int SET_SIZE = 2;
	void applyShape(sun.java2d.pipe.Region var0);
	boolean canDetermineObscurity();
	int checkImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	void coalescePaintEvent(java.awt.event.PaintEvent var0);
	void createBuffers(int var0, java.awt.BufferCapabilities var1) throws java.awt.AWTException;
	java.awt.Image createImage(int var0, int var1);
	java.awt.Image createImage(java.awt.image.ImageProducer var0);
	java.awt.image.VolatileImage createVolatileImage(int var0, int var1);
	void destroyBuffers();
	void dispose();
	void flip(int var0, int var1, int var2, int var3, java.awt.BufferCapabilities.FlipContents var4);
	java.awt.Image getBackBuffer();
	java.awt.image.ColorModel getColorModel();
	java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	java.awt.Graphics getGraphics();
	java.awt.GraphicsConfiguration getGraphicsConfiguration();
	java.awt.Point getLocationOnScreen();
	java.awt.Dimension getMinimumSize();
	java.awt.Dimension getPreferredSize();
	java.awt.Toolkit getToolkit();
	void handleEvent(java.awt.AWTEvent var0);
	boolean handlesWheelScrolling();
	boolean isFocusable();
	boolean isObscured();
	boolean isReparentSupported();
	void layout();
	void paint(java.awt.Graphics var0);
	boolean prepareImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	void print(java.awt.Graphics var0);
	void reparent(java.awt.peer.ContainerPeer var0);
	boolean requestFocus(java.awt.Component var0, boolean var1, boolean var2, long var3, sun.awt.CausedFocusEvent.Cause var4);
	void setBackground(java.awt.Color var0);
	void setBounds(int var0, int var1, int var2, int var3, int var4);
	void setEnabled(boolean var0);
	void setFont(java.awt.Font var0);
	void setForeground(java.awt.Color var0);
	void setVisible(boolean var0);
	void setZOrder(java.awt.peer.ComponentPeer var0);
	void updateCursorImmediately();
	boolean updateGraphicsData(java.awt.GraphicsConfiguration var0);
}

