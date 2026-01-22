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


for loop_count in range(20):
    print(f"Loop {loop_count + 1}")
    try:
        button_elements = d.xpath(f'//*[@text=" 去完成"]').all()
        task_elements = d.xpath(f'//*[@text=" 去完成"]/../../preceding-sibling::*[1]/*[1]/*[2]').all()
        print(f"Found {len(button_elements)} '去完成' buttons")
        print(f"Found {len(task_elements)} tasks")

        if len(button_elements) == 0:
            button_elements = d.xpath(f'//*[@text="+5"]').all()
            task_elements = d.xpath(f'//*[@text="+5"]/../../preceding-sibling::*[1]/*[1]/*[2]').all()
        print(f"Found {len(button_elements)} '+5' buttons")
        print(f"Found {len(task_elements)} tasks")

        if len(button_elements) == 0 or len(task_elements) == 0:
            raise Exception("Required elements not found")
        elif len(button_elements) != len(task_elements):
            raise Exception("Mismatch in number of buttons and tasks")

        for i in range(len(button_elements)):
            task_text = task_elements[i].info["text"]
            print(f"the task name is: {task_text}")
            # if task_text is 滑动浏览优品会场15秒
            if "滑动浏览优品会场15秒" in task_text:
                button_elements[i].click()
                print(f"Clicked button for task: {task_text}")
                time.sleep(3)
                d.press("back")
                time.sleep(3)
                d.press("back")
                for i in range(5):
                    d.swipe(500, 1000, 500, 500)
                    time.sleep(4)
                d.press("back")                
                continue
            elif "滑动浏览15秒红包会场" in task_text:
                button_elements[i].click()
                print(f"Clicked button for task: {task_text}")
                time.sleep(3)
                d.press("back")
                time.sleep(3)
                d.press("back")
                time.sleep(3)
                d.press("back")
                for i in range(5):
                    d.swipe(500, 1000, 500, 500)
                    time.sleep(4)
                d.press("back")                
                continue
            else:
                continue
    except Exception as e:
        print(f"Click failed: {e}, falling back to shell")
        d.shell(f"am start -a android.intent.action.VIEW -d '{url}'")
        time.sleep(2)
        d.swipe(540, 1600, 540, 1300)
        time.sleep(5)
