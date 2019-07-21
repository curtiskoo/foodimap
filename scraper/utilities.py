import json
import pymongo


def save_json(fn, data):
    with open(fn, 'w') as f:
        json.dump(data, f)
    print('Saved to: {}'.format(fn))


def load_json(fn):
    with open(fn) as f:
        d = json.load(f)
        return d


passwords = load_json("../resources/passwords.json")


def init_mongo_client():
    user = passwords['mongo_user']
    pwd = passwords['mongo_pwd']
    db_string = passwords['mongo_srv']
    connection_string = "mongodb+srv://{}:{}@{}".format(user, pwd, db_string)
    client = pymongo.MongoClient(connection_string)
    return client

