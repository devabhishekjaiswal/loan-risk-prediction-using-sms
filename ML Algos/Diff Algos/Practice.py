import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn import metrics

iris = load_iris()

# print iris
# vec = DictVectorizer()



#
#
df = pd.read_json('my.json', orient='list')
# df = pd.read_json('new.json', orient='list')
print df[['credited', 'debited', 'rechargeAmount', 'label']]
# df = df[['credited', 'debited', 'rechargeAmount', 'label']]

# df.to_csv('ANNcsv', sep='\t', encoding='utf-8')
# df = df['data']
# print df
#
# df = pd.DataFrame.to_json(df['data'])  #returns data frame
# print df
# #
# X = vec.fit_transform(df).toarray()

# print(X)

target = []
for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print len(target)
# print df.columns
dfdata = df[['credited', 'debited', 'rechargeAmount']]

target_Train = target[:450]
Train = dfdata[:450]
# print X

clf = tree.DecisionTreeClassifier()

# _______Training our data___________________

clf.fit(Train, target_Train)
# _________________________________________

test = dfdata[450:]

# test = np.array([541.0, 4545.0, 4054.0]).reshape(-1, 3)
print clf.predict(test)
print target[450:]
accuracy = metrics.accuracy_score(clf.predict(test), target[450:])
print metrics.confusion_matrix(clf.predict(test), target[450:])


print accuracy