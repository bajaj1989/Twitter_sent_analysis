# Twitter_sent_analysis

Apache Flume is used as a medium to extract data from twitter. The flume configuration file is attached in the repository. 
One needs to create a developer's account in twitter to get the access token and password. In the properties file you need to mention the keywords based on which the tweets are to be extracted. The data extracted by flume is in json format and contains all the user and tweet related information in json format. The sink I used here is hdfs where all the data is dumped.


Next we create a hive external table(tweets_apple) over the data extracted from twitter. The hive script for the same is uploaded in the repository. Again we create a hive managed table(tweet) containing only username, time and tweet. With this ETL phase of twitter data extraction is over.


Next we need to process and analyze this data. We use mapreduce for this purpose. The main idea is to process all the tweets for a particular time(in minutes) and display the positive, negative, neutral and overall sentiment of the tweets for that minute. We have sample files here attached in this repo each of which contains positive and negative words. The first aim is to remove all the stop words in the tweet. Then we remove any unwanted characters from the tweet as in chinese characters. Next we lookup for each word in the above mentioned files and mark it as a posive, negative or neutral. The mapper emits time as a key and the sentiment of a tweet as value. The reducer then counts the overall sentiment of all the tweets for that minute. All the three lookup files are added to the distributed cache and these files are then retrieved from the cache in each of the map task during setup. When a file is added to the distributed cache, it is copied to the local filesystem of each mapper and the map task then retrieves it from its local.


Next work to be done::

Find sentiment of all the emoticons in the tweet.

Plot a graph of the ultimate result.
