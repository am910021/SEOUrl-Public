@echo off
@title 取得Archive資料
set CLASSPATH=.;dist\*
java -Xmx1024m seourl.SEOUrl
pause