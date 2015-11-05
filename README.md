# metadata-diff
metadata-diff : Tool to compare two database tables. Customized diffkit.

#Eclipse argument
-------------------------
# csv generation and perform diff
-metadatadiff -config E:/diff-svn/metadata-config.csv -properties E:/diff-svn/metadata-diff.properties

# csv generation /one sided..that is left
-generatecsv -config E:/diff-svn/metadata-config.csv -properties E:/diff-svn/metadata-diff.properties

# perform diff based on lhs and lhs already generated CSV
-diffcsv -config E:/diff-svn/metadata-config.csv -properties E:/diff-svn/metadata-diff.properties


#Linux: gnerate jar file of project , metadata-diff-linux.jar
-------------------------
metadata-diff-linux.jar -jar -metadatadiff -config /opt/metadata-config.csv -properties /opt/metadata-diff.properties

