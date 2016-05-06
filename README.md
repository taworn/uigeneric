UI Generic
----------

This is program for example which I wrote it.  I wrote this program to describe Android basic programming.  This program contains 3 parts: Sample, Sample Server and Settings pages.

**Sample**

This is a program part to explain about basic.  Start from base UI, RecyclerView, Navigation Drawer, until to SQLite, sort, search and etc.

**Sample Server**

A program part which nearly close with Sample part, but it save database into server.  This program contains bug, especially, when program reach internet unstable or switch orientation.  But, hey, it's just a demo program, right. :)

**Settings**

This part is assembly of options, they are:

 - Database file location: you can choose between Internal or External.  Choose External will allow you to use SQLite Manager to open database.
 - Server address or IP: you can specify which server/path.  The server will be connected with Sample Server.
 - Profile name: demonstrate to show in Navigation Drawer.
 - Profile email: same demonstration.

**How to Setup Apache, MySQL, PHP**

This is an install programs on server.  I used XAMPP Lite (http://portableapps.com/apps/development/xampp).  After run AMP (Apache, MySQL, PHP) started up, next step:

 - Create database name "android", collation "utf8_unicode_ci".
 - Create table by run script in uigeneric/web/schema.sql.
 - Copy all files in uigeneric/web/resttest to web server, my settings is xampp/htdocs.
 - Open file xampp/htdocs/resttest/api/db.php to edit IP, username, password, if need.
 - Open browser and type: http://127.0.0.1/resttest/sample.php.

This web program is just a basic program.  It can add, edit, delete and help you see data in Android.

**Test**

This programs will run Testsuite (JUnit), ApplicationSuite and ApplicationSuiteServer (Espresso) to tests various part of program.  About ApplicationSuiteServer, you have to build apps, open Settings and set server address so the suite can run.

**Document**

You can open JavaDoc and build it to generate document.

**Thank**

Thank you Google to create Android for me.
Thank you for me that create this program:

- https://design.google.com/icons/index.html
- https://github.com/codepath/android_guides/wiki/Android-Testing-Options
- http://stackoverflow.com/
- http://www.vogella.com/
- http://www.tutorialspoint.com/
- and many more...

And finally, thank you GitHub (https://github.com/taworn/) for my repositories.

----------

Taworn T.

My humble English is very poor, I'm not English native, sorry. T_T

