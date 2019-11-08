@echo off
@title SeoUrl
set CLASSPATH=.;dist\*
java -server -DIP=0.0.0.0 -Xmx6144m seourl.SEOUrl
pause