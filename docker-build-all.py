import os
import subprocess

def find_all(name, path):
    result = []
    for root, dirs, files in os.walk(path):
        if name in files:
#            result.append(os.path.join(root, name))
            result.append(os.path.abspath(root))
    print(result)
    return result

def compose_all(roots):
    for root in roots:
        print(root)
        os.chdir(root)
#        os.system("docker-compose up -d")
        ret = subprocess.run("docker-compose up -d",shell=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE,encoding="utf-8")
        while ret.returncode != 0:
            print("error:",ret.stderr)
            ret = subprocess.run("docker-compose up -d",shell=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE,encoding="utf-8")
        os.system("docker-compose down")

result = find_all("docker-compose.yml", ".") 
compose_all(result)