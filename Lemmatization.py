import en_core_web_sm
import da_core_news_sm
import json

engLemma = en_core_web_sm.load()
danLemma = da_core_news_sm.load()

jsonTest= {"Title:":"Lol","Year":"2013","Text":"This is a test for the lemmatizer"}

#load_model = spacy.load('en', disable=['parser','ner'])


My_text = "Vi begynder med 4 ugers sprint fra fredag næste uge mener jeg, når vi begynder på sprint 3. Og det er da ikke search engine alle skal have rørt ved, er det ikke det færdige produkt alle skal have rørt ved på en eller anden måde? Fra mødet i tirsdags lød det til at der ikke manglede meget, for at search engine faktisk virkede, og derfor var det aftalt at gruppe G selv stod for at få de sidste små ting op og køre. De vil uddeleger opgaver når det blev nødvendigt. Alle andre grupper der ikke havde noget at lave i forbindelse med search engine, er standby grupper i forhold til search engine."

doc = engLemma(jsonTest["Text"])

print(" ".join([token.lemma_ for token in doc]))
