import matplotlib.pyplot as plt
from math import cos, sin, pi, sqrt
import numpy
from utilities import init_mongo_client, save_json


def points_on_circle(nc, radius, center):
    """
        Finding the x,y coordinates on circle, based on given angle
    """
    # center of circle, angle in degree and radius of circle
    #     center = [0,0]
    points = []

    for n in numpy.arange(0, 2, 2 / nc):
        angle = pi * n
        x = center[0] + (radius * cos(angle))
        y = center[1] + (radius * sin(angle))
        points.append((x, y))

    r = get_distance(points[0], points[1]) / 2

    return points, r


def convert_ll_v2(curr, cart):
    lat = curr[0]
    lon = curr[1]

    dx = cart[0]
    dy = cart[1]

    dLat = dy / 111111
    dLon = dx / (111111 * (cos(pi * lat / 180)))

    lat1 = lat + dLat
    lon1 = lon + dLon
    return (lat1, lon1)


def get_distance(c1, c2):
    dx = (c1[0] - c2[0]) ** 2
    dy = (c1[1] - c2[1]) ** 2
    return sqrt(dx + dy)


def get_p_closest_to_origin(c, p, r):
    srx = c[0] + r * (p[0] - c[0]) / get_distance(p, c)
    sry = c[1] + r * (p[1] - c[1]) / get_distance(p, c)
    return (srx, sry)


def get_midpoint(c1, c2):
    dx = (c1[0] + c2[0]) / 2
    dy = (c1[1] + c2[1]) / 2
    return (dx, dy)


def is_outside_circle(main, sub, un=0):
    d = get_distance(main, sub)
    return d > (main[2] + sub[2] - un)


def is_inside_circle(main, sub, un=0):
    d = get_distance(main, sub)
    return main[2] > (d + sub[2] - un)


def is_inside_any_circles(sub, lst, un=0):
    for item in lst:
        if item == sub:
            continue
        if is_inside_circle(item, sub, un):
            #             print(item, sub)
            return True
    return False


def recursive_fractal(start, r):
    #     if r <= limit:
    #         return lst
    #     print(start, r)
    points, sr = points_on_circle(4, r * 0.65, start)
    #     print(sr, len(points))
    #     print(points)
    points = list(map(lambda x: (x[0], x[1], r / 2), points))

    ngp = list(map(lambda x: get_p_closest_to_origin(start, x, r), points))
    ngp.append(ngp[0])
    # print(ngp)
    ngp2 = []
    for num in range(len(ngp) - 1):
        n1 = ngp[num]
        n2 = ngp[num + 1]
        mid = get_midpoint(n1, n2)
        ngp2.append(mid)
    ngp2 = list(map(lambda x: get_p_closest_to_origin(start, x, r), ngp2))
    ngp2 = list(map(lambda x: (x[0], x[1], r / 3), ngp2))

    res = points + ngp2 + [(start[0], start[1], r / 4)]
    #     print(res)
    for item in res:
        if item[2] > limit:
            res += recursive_fractal((item[0], item[1]), item[2])
        else:
            return res
    #             return recursive_fractal((item[0], item[1]), item[2]/2, res)

    return res

# get circle data points

r = 20250
limit = 1000
start = (0, 0)
curr = (49.148988, -123.056310)  # centre of master circle

fig, ax = plt.subplots()  # note we must use plt.subplots, not plt.subplot
circle1 = plt.Circle(start, r, color='r', fill=False)
ax.add_artist(circle1)


res = recursive_fractal(start, r)
res = list(filter(lambda x: x[2] <= limit, res))
print(len(res))
res = list(filter(lambda x: not (is_outside_circle((start[0], start[1], r), x, r * 0.01)), res))
print(len(res))
# res = list(filter(lambda x: not(is_inside_any_circles(x, res)) , res))
print(len(res))
print(list(set(list(map(lambda x: x[2], res)))))
print((start[0], start[1], r))

# xy = list(map(lambda x: (x[0], x[1]), res))
# print(len(xy))
# print(len(set(xy)))

for coord in res:
    temp = plt.Circle((coord[0], coord[1]), coord[2], color='blue', fill=True)
    ax.add_artist(temp)


axis = r * 1.5
plt.axis([-1 * axis, axis, -1 * axis, axis])
plt.gca().set_aspect('equal', adjustable='box')
fig = plt.gcf()
fig.set_size_inches(12, 12)
plt.draw()
plt.show()

# Convert cartesian data points into geo lat long points

geo = list(map(lambda x: convert_ll_v2(curr, x), res))
res = list(zip(list(map(lambda x: x[0], geo)), list(map(lambda x: x[1], geo)), list(map(lambda x: x[2], res))))
res = list(map(lambda x: (x[2], x[0], x[1]), res))

new_res = list(map(lambda x: {"_id": "{}_{}".format(x[1], x[2]), "lat": x[1], "lng": x[2], "radius": x[0]}, res))

print(len(new_res))

ll = []
acc = []
for coord in new_res:
    x = coord['lat']
    y = coord['lng']
    if (x, y) not in acc:
        temp_lst = list(filter(lambda i: i['lat'] == x and i['lng'] == y, new_res))
        temp_lst = sorted(temp_lst, key=lambda i: i['radius'], reverse=True)[0]
        ll.append(temp_lst)
        acc.append((x, y))

new_res = ll
print(len(new_res))

save_json("../resources/static_ll_points.json", new_res)

# Insert into database
client = init_mongo_client()
db = client.restaurant_db

for point in new_res:
    db.ll_points.insert_one(point)

