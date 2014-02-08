This is an application for installing base nano system bundles.
It installs core felix runtime, nanohttpd, engender and mongodb java driver as
core nanohttpd system. Also it installs mangodb based session handler

The installation is perfermed by downloading maven artifacts and dependencies
into the predefined folders.

The nanohttpd based web applications should be deployed at folder apps. It will
deployed into felix automatically.

The project result jar is minimized and deployed at:

http://maven2.sanaldiyar.com/service/local/artifact/maven/redirect?r=sanaldiyar-snapshot&g=com.sanaldiyar.projects.nanohttpd&a=nanoinstaller&v=LATEST

For usage you can run java -jar nanoinstaller.jar -h

Kazım SARIKAYA
kazimsarikaya@sanaldiyar.com