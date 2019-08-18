# DartIT Online Store



Как собрать:
	
1. Установить Oracle DB 18c.
2. Установить Tomcat 8.
3. Запустить скрипты из database_setup в следующем порядке: CLEAR, SETUP, TEST_FILL.
4. Открыть проект в Eclipse.
5. Вписать данные о базе в `DatabaseManager.java`.
6. Собрать ROOT.war используя mvn compile war:war.
7. Поместить ROOT.war в Tomcat/webapps.
8. Запустить Tomcat.

Хост для проверки: http://pomas.mywire.org