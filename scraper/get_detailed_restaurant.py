from utilities import init_mongo_client, get_yelp_headers
import requests
from pprint import pprint


def get_detail_by_id(id):
    url = "https://api.yelp.com/v3/businesses/{}".format(id)

    null = None
    false = False
    true = True

    s = requests.get(url, headers=headers)
    s = eval(s.text)
    return s


def handle_yelp_return(id_num, dic):
    if "error" in dic:
        # error = handle_error(id_num, dic['error'])
        # return error
        # if error == "BUSINESS_UNAVAILABLE":
        #     return error
        print("Error: {} - {}".format(dic['error']['code'], id_num))
        return dic['error']

    if "hours" in dic:
        del dic['hours'][0]['is_open_now']

    # if "id" in dic:
    del dic['id']

    dic['detailed'] = True
    return True


def handle_error(id, dic):
    code = dic['code']
    if code == "BUSINESS_MIGRATED":
        print(code)
        test = get_detail_by_id(dic["new_business_id"])
        handle_yelp_return(dic["new_business_id"], test)
        insert_detailed_record(dic["new_business_id"], test)
    elif code == "INTERNAL_ERROR":
        print(code)
        test = get_detail_by_id(id)
        handle_yelp_return(id, test)
        insert_detailed_record(id, test)
    elif code == "BUSINESS_UNAVAILABLE":
        print(code)
        return code
    else:
        raise Exception("Error code {} - {} not supported".format(dic, id))


def insert_detailed_record(id, dic):
    db.yelp.replace_one({"_id": id}, dic, upsert=True)
    print("Done: {}".format(id))


def get_refresh_or_insert(b):
    if b:
        return list(db.yelp.find())
    else:
        return list(db.yelp.find({"detailed": {"$exists": False}}))


headers = get_yelp_headers()
client = init_mongo_client()
db = client.restaurant_db

record_lst = get_refresh_or_insert(False)  # toggle this to refresh/insert
for record in record_lst:
    inserted = False
    id_num = record['_id']
    detail_record = get_detail_by_id(id_num)
    new_id = None
    if 'id' in detail_record:
        new_id = detail_record['id']
    yelp_return_code = handle_yelp_return(id_num, detail_record)

    while yelp_return_code != True:
        if yelp_return_code['code'] == "BUSINESS_UNAVAILABLE":
            record['yelp_unavailable'] = True
            insert_detailed_record(id_num, record)
            inserted = True
            break
        elif yelp_return_code['code'] == "BUSINESS_MIGRATED":
            detail_record = get_detail_by_id(yelp_return_code["new_business_id"])
            yelp_return_code = handle_yelp_return(yelp_return_code["new_business_id"], detail_record)
            # insert_detailed_record(yelp_return_code["new_business_id"], test)
            # inserted = True
            # break
        elif yelp_return_code['code'] == "INTERNAL_ERROR":
            detail_record = get_detail_by_id(id_num)
            if 'id' in detail_record:
                new_id = detail_record['id']
            yelp_return_code = handle_yelp_return(id_num, detail_record)
            print(yelp_return_code)
            # insert_detailed_record(id, test)
            # continue
        elif yelp_return_code['code'] == "NOT_FOUND":
            inserted = True
            break
        else:
            raise Exception("ID: {} | {}".format(id_num, yelp_return_code))

    if inserted:
        continue
    # print(detail_record)
    if new_id == id_num:
        insert_detailed_record(id_num, detail_record)
    else:
        raise Exception("Something wrong with detailed record: {} {} {}".format(detail_record, new_id, id_num))


