#!/bin/bash
export CLASSPATH=".:dist/*"
java -Xmx6144m -DIP=0.0.0.0 seourl.SEOUrl
read -n 1 -p "Press any key to continue..."
