# -*- coding: utf-8 -*-
"""
Created on Tue Mar 16 16:10:29 2021

@author: madalin.ghita
"""

import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from os.path import dirname, join
import pymysql
filenameBook = join(dirname(__file__), "books_no_comma.csv")
filenameRating = join(dirname(__file__), "rating.csv")

books = pd.read_csv(filenameBook, sep=',', error_bad_lines=False, encoding='ISO-8859-1')
ratings = pd.read_csv(filenameRating, sep=',', error_bad_lines=False, encoding='ISO-8859-1')

# db = pymysql.connect(host='10.0.2.2 ', port=3306, user='root', passwd='daniela27', db='test_app', bind_address='0.0.0.0.0')
# cursor = db.cursor()
# sql = "SELECT * FROM BOOKS"
# cursor.execute(sql)
#
# resultsBooks = cursor.fetchall()
#
# books = pd.DataFrame(resultsBooks, columns=["book_id", "title", "authors", "original_publication_year", "genres"])
#
#     #df.genres.str.replace(';;', '', regex=True)
# books.genres = books.genres.str.replace('[;,\r]', '', regex=True)
#
#     #print(df)
#
# cursor = db.cursor()
# sql = "SELECT * FROM RATINGS"
# cursor.execute(sql)
#
# resultsRatings = cursor.fetchall()
#
# ratings = pd.DataFrame(resultsRatings, columns=["user_id", "book_id", "rating"])
# cursor.close()
# db.close()

combine_book_rating = pd.merge(ratings, books, on='book_id')
#print(combine_book_rating)

book_ratingCount = (combine_book_rating.
     groupby(by = ['title'])['rating'].
     count().
     reset_index().
     rename(columns = {'rating': 'totalRatingCount'})
     [['title', 'totalRatingCount']]
    )

#print(book_ratingCount)

rating_with_totalRatingCount = combine_book_rating.merge(book_ratingCount, left_on = 'title', right_on = 'title', how = 'left')

#print(rating_with_totalRatingCount)

pd.set_option('display.float_format', lambda x: '%.3f' % x)
#print(book_ratingCount['totalRatingCount'].describe())
#print(book_ratingCount['totalRatingCount'].quantile(np.arange(.9, 1, .01)))

popularity_threshold = 100
rating_popular_book = rating_with_totalRatingCount.query('totalRatingCount >= @popularity_threshold')
#print(rating_popular_book)

rating_popular_book = rating_popular_book.drop_duplicates(['user_id', 'title'])
#print(rating_popular_book)

rating_popular_book_pivot = rating_popular_book.pivot(index = 'title', columns = 'user_id', values = 'rating').fillna(0)
rating_popular_book_matrix = csr_matrix(rating_popular_book_pivot.values)
#print(rating_popular_book_pivot)
#print(type(rating_popular_book_pivot))
from sklearn.neighbors import NearestNeighbors

model_knn = NearestNeighbors(metric = 'cosine', algorithm = 'brute')
model_knn.fit(rating_popular_book_matrix)

# ind = [1, 2, 600]
# recommendation = []
# for i in range(0, len(ind)):
#     query_index = ind[i]
#     distances, indices = model_knn.kneighbors(rating_popular_book_pivot.iloc[query_index, :].values.reshape(1, -1), n_neighbors = 4)
#
    
    # print(type(rating_popular_book_pivot.iloc[query_index, :]))
    # print(query_index)
    #
    # print(rating_popular_book_pivot.iloc[query_index, :].name)
    # print(distances)
    # print(type(rating_popular_book_pivot.shape[0]))


   # ind = [1, 2, 600]
def checkBooks(initial_set, recommendation):
    r = []
    for x in recommendation:
        if x not in initial_set:
            r.append(x)
    return r


def computeRec(ind):
    initial_set = []
    recommendation = []

    res_data = []

    for i in range(0, len(ind)):
        query_index = ind[i]
        distances, indices = model_knn.kneighbors(rating_popular_book_pivot.iloc[query_index, :].values.reshape(1, -1), n_neighbors = 4)


        for i in range(0, len(distances.flatten())):
            if i == 0:
                print('Recommendations for {0}:\n'.format(rating_popular_book_pivot.index[query_index]))
                initial_set.append(rating_popular_book_pivot.index[query_index])
            else:


                recommendation.append((rating_popular_book_pivot.index[indices.flatten()[i]]))
                
    rec = checkBooks(initial_set, recommendation)
    return rec
