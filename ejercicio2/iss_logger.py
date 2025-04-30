#-*- coding: utf-8 -*-    #ASCII karaktereak erabiltzeko

#import requests
import urllib2
import json
import time
import logging

#Konfiguratu log-ak fitxategi batean. Configurar los log en un fichero. 
logging.basicConfig(filename="iss_location.log", level=logging.INFO, format="%(asctime)s - %(message)s")

#API Open Notify konfiguratu. Configurar API Open Notify
URL = "http://api.open-notify.org/iss-now.json"


def fetch_iss_location():
  try:
    response = urllib2.urlopen(URL)
    data = json.load(response)

    latitude = data["iss_position"]["latitude"]
    longitude = data["iss_position"]["longitude"]

    log_entry = "{0}, {1}".format(latitude, longitude)
    logging.info(log_entry)
    print(log_entry)
  except urllib2.HTTPError as e:
    logging.error("API kontsultatzean errorea suertatu da: {0}".format(e.code))
  except urllib2.URLError as e:
    logging.error ("Konexio errores: {0}".format(e.reason))

#Egikaritu 10 segundoro
while True:
 fetch_iss_location()
 time.sleep(2)
