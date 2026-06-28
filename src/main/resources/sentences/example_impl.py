# Example implementation of sentence generator
#
# You can use this to test your own sentence structure
# without modifying Java codes.


# pip install pyjson5
import pyjson5

LOCALE = "ko_kr"

KEYS = {"LOCAL_NETWORK_ACCESS", "AR_SESSION", "MIC_STREAM", "MIDI_SYSEX", "GEOLOCATION"}
ORIGIN = "google.com"

with open(f"{LOCALE}.json", "r") as fp:
    FORMAT = pyjson5.load(fp)

by_group = {}
for key in KEYS:
    group = FORMAT["verb"][key]
    if group in by_group:
        by_group[group].append(key)
    else:
        by_group[group] = [key]

print(by_group)

def generate_list(messages):
    if len(messages) == 1:
        return FORMAT["format"]["one"] % (messages[0])
    if len(messages) >= 2:
        if len(messages) == 2:
            rest = FORMAT["format"][f"two"] % \
                (messages[0], messages[1])
        else:
            rest = FORMAT["format"][f"three"] % \
                (", ".join(messages[:-2]), messages[-2], messages[-1])
        return rest

    return None

by_group_s = []
for group, keys in by_group.items():
    string = generate_list(list(map(FORMAT["rest"].get, keys)))

    # Python doesn't support Java style string formatting
    if "%1$s" in FORMAT["format"]["verb_and_rest"]:
        by_group_s.append(FORMAT["format"]["verb_and_rest"]
                          .replace("%1$s", group)
                          .replace("%2$s", string))
    else:
        by_group_s.append(FORMAT["format"]["verb_and_rest"] % (group, string))

print(by_group_s)

print(FORMAT["format"]["full"] % (ORIGIN, generate_list(by_group_s)))