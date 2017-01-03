#!/bin/bash
if [ ! -d notes ]; then
  mkdir notes
  if [ $? -ne 0 ] ; then
    echo "couldn't create notes directory"
    exit
  fi
fi
if [ ! -d notes/WEB-INF ]; then
  mkdir notes/WEB-INF
  if [ $? -ne 0 ] ; then
    echo "couldn't create notes/WEB-INF directory"
    exit
  fi
fi
if [ ! -d notes/WEB-INF/lib ]; then
  mkdir notes/WEB-INF/lib
  if [ $? -ne 0 ] ; then
    echo "couldn't create notes/WEB-INF/lib directory"
    exit
  fi
fi
rm -f notes/WEB-INF/lib/*.jar
cp dist/Notes.jar notes/WEB-INF/lib/
cp web.xml notes/WEB-INF/
jar cf notes.war -C notes WEB-INF 
echo "NB: you MUST copy the contents of tomcat-bin to \$tomcat_home/bin"
