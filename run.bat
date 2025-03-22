@echo off
set CLASSPATH=bin
javac -d bin -cp demo/src/main/java demo/src/main/java/com/example/Yalex.java
java -cp bin com.example.Yalex %1
