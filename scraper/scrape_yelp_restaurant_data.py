import requests
import math
from utilities import init_mongo_client, load_json, get_yelp_headers

def insert_records_yelp(db, lst):
    """
        db is the database object from pymongo, lst is the records returned from the yelp api
         - this call replaces existing yelp records that are already in the db with the same id.
         - we can decide to skip the records that are already in, but also have an option to update (data refreshes)
    """
    for record in lst:
        record['_id'] = record['id']
        del record['id']
        del record['is_closed']
        del record['distance']
        db.yelp.replace_one({"_id": record['_id']}, record, upsert=True)

    print('Done Inserting Records!')


def get_local_restaurants_yelp(headers, lat, lng, radius, offset=0):
    """client_secret is your yelp api key"""
    radius = math.ceil(radius)
    if radius > 1000:
        raise Exception("Radius should not be > 1000m: {}m".format(radius))

    print("Scraping for: ({}, {}) {}m".format(lat, lng, radius))
    # headers = get_yelp_headers()
    data = {
        "categories": "food,restaurants",
        "latitude": lat,
        "longitude": lng,
        "radius": radius,
        "offset": offset,
        "limit": "50"
    }

    null = None
    false = False
    true = True

    url = "https://api.yelp.com/v3/businesses/search"
    s = requests.get(url, params=data, headers=headers)
    s = eval(s.text)

    res = []

    curr = s['businesses']
    res += curr
    total = s['total']

    print("Offset: {} | Current: {} | Result: {} | Total: {}".format(data['offset'], len(curr), len(res), total))

    while len(res) < total:
        data['offset'] += len(curr)
        s = requests.get(url, params=data, headers=headers)
        s = eval(s.text)
        curr = s['businesses']
        res += curr
        print("Offset: {} | Current: {} | Result: {} | Total: {}".format(data['offset'], len(curr), len(res), total))

    if len(res) != total:
        raise Exception(
            "Error occurred - actual result and yelp given total not equal: res - {} | total - {}".format(len(res),
                                                                                                          total))

    print('Done!')
    return res


client = init_mongo_client()
db = client.restaurant_db
test = list(db.ll_points.find({}))
yelp_headers = get_yelp_headers()
# test.reverse()
# test = test[:3000] #only grab rest of all coords from ll_points collection
acc = 0
for item in test:
    acc += 1
    x = get_local_restaurants_yelp(yelp_headers, item['lat'], item['lng'], item['radius'])
    insert_records_yelp(db, x)
    print("Count: {}".format(acc))
    print()
