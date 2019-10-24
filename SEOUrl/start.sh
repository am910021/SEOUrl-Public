#!/bin/bash
export CLASSPATH=".:dist/*"
java -Xmx1024m seourl.SEOUrl
read -n 1 -p "Press any key to continue..."
