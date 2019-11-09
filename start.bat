@echo off
@title SeoUrl
set CLASSPATH=.;dist\*
java -server -DIP=0.0.0.0 -Xmx8182m seourl.SEOUrl
pause
