import json
import time

import numba
f = open("items.json",encoding="utf-8")
dump = json.loads(f.read())
print(dump)