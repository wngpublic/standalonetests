javac -d bin -cp src src/*.java
java -cp bin StandaloneTest

git init
git add src/
git commit -m "commit 1"
curl -u 'wngpublic' https://api.github.com/user/repos -d '{"name":"standalonetests"}'
git remote add origin https://github.com/wngpublic/standalonetests.git
git push https://wngpublic@github.com/wngpublic/standalonetests.git 

