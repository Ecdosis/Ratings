#!/bin/bash
if [ ! -d ratins ]; then
  mkdir ratings
  if [ $? -ne 0 ] ; then
    echo "couldn't create ratings directory"
    exit
  fi
fi
if [ ! -d ratings/WEB-INF ]; then
  mkdir ratings/WEB-INF
  if [ $? -ne 0 ] ; then
    echo "couldn't create ratings/WEB-INF directory"
    exit
  fi
fi
if [ ! -d ratings/WEB-INF/lib ]; then
  mkdir ratings/WEB-INF/lib
  if [ $? -ne 0 ] ; then
    echo "couldn't create ratings/WEB-INF/lib directory"
    exit
  fi
fi
rm -f ratings/WEB-INF/lib/*.jar
cp dist/Ratings.jar ratings/WEB-INF/lib/
cp web.xml ratings/WEB-INF/
jar cf ratings.war -C ratings WEB-INF 
echo "NB: you MUST copy the contents of tomcat-bin to \$tomcat_home/bin"
