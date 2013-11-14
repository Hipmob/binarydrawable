/**
 * Copyright (C) 2013 Orthogonal Labs, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hipmob;

import java.io.*;

/**
 * <p>Outputs a Java source file that can return a BitmapDrawable instance based on an input image file.</p>
 *
 * @author Femi Omojola <femi@hipmob.com>
 */
public class GenerateBinaryDrawable
{
    public static void main(String[] args) throws Exception
    {
	/*
	 * Calling convention: java -jar binarydrawable.jar -i <image file> -o <class name> -p <package name>
	 *
	 * For example: 
	 *
	 * java -jar binarydrawable.jar -i disconnected.png -o Disconnected -p com.hipmob.android.binary
	 *
	 * or
	 *
	 * java -jar binarydrawable.jar -i icon.png -o Icon
	 */
	String className = null, fileName = null, packageName = null;
	for(int i=0;i<args.length;i++){
	    if("-i".equals(args[i])){
		++i;
		if(i<args.length) fileName = args[i];
	    }else if("-o".equals(args[i])){
		++i;
		if(i<args.length) className = args[i];
	    }else if("-p".equals(args[i])){
		++i;
		if(i<args.length) packageName = args[i];
	    }
	}

	if(fileName == null){
	    System.err.println("No image file specified.");
	    System.err.println("Usage: java -jar binarydrawable.jar -i <image file> -o <class name> -p <package name>");
	    return;
	}

	File f = new File(fileName);
	if(!f.exists()){
	    System.err.println("The specified image file ("+fileName+") could not be found.");
	    return;
	}

	// accept JPG and PNG formats.
	if(!(f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg"))){
	    System.err.println("The specified image file ("+fileName+") must be a JPG or PNG formatted image.");
	    return;
	}

	if(className == null){
	    System.err.println("No class name specified.");
	    System.err.println("Usage: java -jar binarydrawable.jar -i <image file> -o <class name> -p <package name>");
	    return;
	}

	File output = new File(className+".java");
	if(output.exists()){
	    System.err.println("The specified output class ("+className+") already exists.");
	    return;	    
	}

	// process it: read it into a byte array
	ByteArrayOutputStream baos = new ByteArrayOutputStream((int)f.length());
	FileInputStream fis = new FileInputStream(f);
	copy(fis, baos);
	fis.close();
	
	// print it out as a hex string
	byte[] b = baos.toByteArray();
	PrintWriter bw = new PrintWriter(new FileWriter(output));
	if(packageName != null){
	    bw.println("package "+packageName+";");
	    bw.println("");
	}
	
	// imports
	bw.println("import android.graphics.Bitmap;");
	bw.println("import android.graphics.BitmapFactory;");
	bw.println("import android.graphics.drawable.BitmapDrawable;");
	bw.println("");

	// class declaration
	bw.println("public class "+className+" {");

	// actual byte array
	bw.println("final static byte[] bytes = new byte[]{");
	for(int j=0;j<b.length;j++){
	    if(j > 0 && j%6 == 0) bw.println("");
	    bw.print(" (byte)0x");
	    bw.print(Integer.toHexString(0xFF&b[j]));
	    if(j <b.length-1) bw.print(",");
	}
	bw.println("};");
	bw.println("");

	// and the output methods
	bw.println("public static final Bitmap getBitmap(){ return BitmapFactory.decodeByteArray(bytes, 0, bytes.length); }");
	bw.println("");
	bw.println("public static final BitmapDrawable getBitmapDrawable(){ return new BitmapDrawable(getBitmap()); }");
	bw.println("");
	
	// aaand, we're done.
	bw.println("}");   
	bw.close();
	
	System.out.println("Generated "+className+".java.");
    }

    static void copy(InputStream is, OutputStream os) throws IOException
    {
	byte buffer[] = new byte[8192];
	int bytesRead, i;
	
	while ((bytesRead = is.read(buffer)) != -1) {
	    os.write(buffer, 0, bytesRead);
	    os.flush();
	}
	os.flush();
    }
}