#!/bin/bash
export CLASSPATH=".:dist/*"
java -server -DIP=0.0.0.0 -Xmx8182m seourl.SEOUrl
read -n 1 -p "Press any key to continue..."
