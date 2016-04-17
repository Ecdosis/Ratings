#!/bin/bash
service tomcat7 stop
cp ratings.war /var/lib/tomcat6/webapps/
rm -rf /var/lib/tomcat7/webapps/ratings
rm -rf /var/lib/tomcat7/work/Catalina/localhost/
service tomcat7 start
