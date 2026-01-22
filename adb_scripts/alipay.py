#!/usr/bin/env python3
import sys
import uiautomator2 as u2
import time

# DEVICE: use first command-line argument if provided, otherwise fallback to default
DEVICE = sys.argv[1] if len(sys.argv) > 1 else "localhost:33333"

try:
    d = u2.connect(DEVICE)
except Exception as e:
    print(f"Failed to connect to {DEVICE}: {e}", file=sys.stderr)
    sys.exit(1)

# Simple check: verify device responds to info query
try:
    info = d.info
except Exception as e:
    print(f"Connected but device not responding: {e}", file=sys.stderr)
    sys.exit(1)

print(f"Connected to device: {info.get('productName')}")

url = "alipays://platformapi/startapp?appId=68687805&url=https%3A%2F%2Frender.alipay.com%2Fp%2Fyuyan%2F180020380000000023%2Fpoint-sign-in.html"

# Whitelist of tasks to allow
WHITELIST = [
    "浏览",
    "看5秒视频领积分",
    "逛15秒安全知识",
    "逛15秒支付有礼领红包",
    "逛15秒精选超值好物",
    "逛15秒芝麻租赁频道",
    "逛15秒芝麻租赁首页",
    "逛一逛余额宝摇钱树",
    "逛一逛摇红包",
    "逛一逛高德打车小程序",
    "逛一逛每日惊喜不断",
    "逛一逛福气鱼塘",
    "逛一逛签到领红包",
    "逛一逛芭芭农场",
    "逛一逛蚂蚁新村",
    "逛一逛蚂蚁森林",
    "逛一逛话费活动",
    "逛一逛领奖励",
    "逛双11会场",
    "逛热卖好货15秒",
    "逛蚂蚁庄园喂小鸡",
    "逛一逛芝麻信用"
]  # Add your whitelist items here

GRAYLIST = [
    "去淘金币赢20亿",
    "逛一逛一淘APP",
    "逛一逛今日头条APP",
    "逛一逛淘宝芭芭农场",
    "逛一逛淘宝视频",
    "逛一逛点淘",
    "逛中国移动领流量",
    "逛双11会场",
    "逛淘宝特价版",
    "逛淘宝视频15秒",
    "逛热卖好货15秒",
    "逛百度天天领现金",
    "逛百度极速版领钱",
    "逛美团刷视频领现金"
]  # Add your whitelist items here

BLACKLIST = [
"下1单",
"下单",
"体验年领5.21%的年金",
"体验省税福利",
"加入商家群组",
"尽享温哥华非凡之旅",
"打卡记录每日好心情",
"玩",
"逛一逛快手",
"逛一逛闲鱼APP",
"逛头条极速版刷视频",
"邀请好友签到领积分"
]

for loop_count in range(48):
    print(f"Loop {loop_count + 1}")
    try:
        button_elements = d.xpath(f'//*[@text=" 去完成"]').all()
        task_elements = d.xpath(f'//*[@text=" 去完成"]/../../preceding-sibling::*[1]/*[1]/*[2]').all()
        print(f"Found {len(button_elements)} '去完成' buttons")
        print(f"Found {len(task_elements)} tasks")

        if len(button_elements) == 0:
            button_elements = d.xpath(f'//*[@text="领积分"]').all()
            task_elements = d.xpath(f'//*[@text="领积分"]/../../../preceding-sibling::*[1]/*[1]/*[2]').all()
            print(f"Found {len(button_elements)} '领积分' buttons")
            print(f"Found {len(task_elements)} tasks")

        if len(button_elements) == 0:
            button_elements = d.xpath(f'//*[@text="+1" or @text="+3" or @text="+5"]').all()
            task_elements = d.xpath(f'//*[@text="+1" or @text="+3" or @text="+5"]/../../preceding-sibling::*[1]/*[1]/*[2]').all()
            print(f"Found {len(button_elements)} '+135' buttons")
            print(f"Found {len(task_elements)} tasks")

        if len(button_elements) == 0 or len(task_elements) == 0:
            raise Exception("Required elements not found")
        elif len(button_elements) != len(task_elements):
            raise Exception("Mismatch in number of buttons and tasks")

        for i in range(len(button_elements)):
            task_text = task_elements[i].info["text"]
            print(f"the task name is: {task_text}")
            
            # Check if task_text is in whitelist
            if any(whitelist_item in task_text for whitelist_item in WHITELIST):
                # Click the button if in whitelist
                button_elements[i].click()
                print(f"Clicked button for task: {task_text}")
                time.sleep(2)  # Small delay between clicks
                break
            # Check if task_text is in blacklist
            elif any(blacklist_item in task_text for blacklist_item in BLACKLIST):
                print(f"Task '{task_text}' is in blacklist, skipping...")
            else: # if not in either list, click it and add to blacklist
                button_elements[i].click()
                print(f"Clicked button for task: {task_text}")
                BLACKLIST.append(task_text)
                time.sleep(2)  # Small delay between clicks
                break

        for i in range(5):
            d.swipe(500, 1000, 500, 500)
            time.sleep(4)
        d.press("back")
        time.sleep(2)
    except Exception as e:
        print(f"Click failed: {e}, falling back to shell")
        d.shell(f"am start -a android.intent.action.VIEW -d '{url}'")
        time.sleep(2)
        d.swipe(540, 1600, 540, 1300)
        time.sleep(5)
        continue

# print out the BLACKLIST at the end
print("BLACKLIST:")
for item in BLACKLIST:
    print(f"- {item}")

