BinaryDrawable
==============

BinaryDrawable is a simple library designed to make it easy to distribute graphic resources (images) inside a JAR file on the Android platform. See http://engineering.hipmob.com/2013/11/14/BinaryDrawable-for-Android/

Traditionally JAR files can include images and other non-code resources. However, the Android development and build process requires that any non-code resources be included in a separate res folder. If you are distributing a library with multiple resources then you generally have to use an Android library project. 

If your library only makes minimal use of images (in the case of the Hipmob library, a couple of icons and placeholder images) it would be nice to just be able to distribute your library as a JAR file. BinaryDrawable makes that possible by converting images into Java source files that can be included in the JAR package. When compiled the resulting class will contain a method that returns a BitmapDrawable: you can then use the BitmapDrawable anywhere a Drawable is accepted (which is just about everywhere on the Android platform).

How
===
To convert any image that can be understood by the Android platform's built-in [BitmapFactory][1] into a BinaryDrawable instance we simply read the image file and create a Java source file that includes a byte array with the image data. The generated class can then use [decodeByteArray][3] to get a [Bitmap][4], and then it is a short hop to a [BitmapDrawable][5].

Build
=====
The library build uses **ant**: you will need to have the ANDROID_SDK environment variable setup (or edit the ***build.xml*** file to point to the right place).

Open a shell in the ***library*** directory and run **ant**. The JAR file produced (***binarydrawable.jar***) will contain the source generator.

Run
===
To convert any image that can be understood by the Android platform's built-in [BitmapFactory][1] into a BinaryDrawable instance:

<pre class="brush: bash">
java -jar binarydrawable.jar -i <image file> -o <class name> -p <package name>
</pre>

Only the image file and the class name are required. If the package name is not specified then the generated class will not have a package statement. As an example, to convert **disconnected.png** into the source file ***Disconnected.java*** in the package ***com.hipmob.android.binary***:

<pre class="brush: bash">
java -jar binarydrawable.jar -i disconnected.png -o Disconnected -p com.hipmob.android.binary
</pre>

or without a package name (which would be odd, but who are we to judge):

<pre class="brush: bash">
java -jar binarydrawable.jar -i icon.png -o Icon
</pre>

Notes
=====
* [There are limits on the output size of any class file][2]: if your source image is over 10KB in size the output file may not compile. This utility is really only useful for small images: if you're dealing with anything large, go with the ***res/drawable*** option.

* The current implementation only accepts JPG and PNG formatted images.

======
Developed by Femi Omojola (femi@hipmob.com) for Orthogonal Labs, Inc. (https://hipmob.com).

[1]: http://developer.android.com/reference/android/graphics/BitmapFactory.html
[2]: http://docs.oracle.com/javase/specs/jvms/se5.0/html/ClassFile.doc.html#9279
[3]: http://developer.android.com/reference/android/graphics/BitmapFactory.html#decodeByteArray%28byte[],%20int,%20int%29
[4]: http://developer.android.com/reference/android/graphics/Bitmap.html
[5]: http://developer.android.com/reference/android/graphics/drawable/BitmapDrawable.html