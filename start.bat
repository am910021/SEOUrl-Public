@echo off
@title SeoUrl
set CLASSPATH=.;dist\*
java -server -Xmx6144m seourl.SEOUrl
pause