/*
这段代码的用途是从知乎日报的API里面获得所有文章的URL
方法是先按照日期遍历每天的API，从返回的json数据中解析出来每篇文章的真实URL
另一个Python脚本被用来从真实URL里面摘取干货部分，之后用calibre生成mobi或者epub文件
*/
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"time"
)

func main() {
	const layout = "20060102" /* 日报用的日期格式*/
	type ZHRBNEWS struct {
		Share_url string
	}
	type ZHRB struct {
		News []ZHRBNEWS	//这个地方要注意大小写-_- 
	}

	// 下面这句话意思是从2013年5月20号知乎日报开始发布的第一天开始，挨个加一天
	for t := time.Date(2013, time.May, 20, 23, 0, 0, 0, time.UTC); t.Before(time.Now()); t = t.AddDate(0, 0, 1) {
		zhihuURL := bytes.Buffer{}
		zhihuURL.WriteString("http://news.at.zhihu.com/api/2/news/before/")
		zhihuURL.WriteString(t.Format(layout))
		/* get得到的结果还需要用json再处理一番 */
		resp, err := http.Get(zhihuURL.String())
		if err != nil {
			fmt.Printf("http.Get出错啦")
		}
		if resp.StatusCode != 200 {
			fmt.Printf("http return != 200\n")
			return
		} else {
			robots, err := ioutil.ReadAll(resp.Body)
			resp.Body.Close()
			if err != nil {
				fmt.Println(err)
				return
			}
			var zhrb ZHRB
			err = json.Unmarshal(robots, &zhrb)
			if err != nil {
				fmt.Println(err)
			}
			for m := range zhrb.News {
				fmt.Printf(zhrb.News[m].Share_url)
				fmt.Printf("\n")
			}
		}
	}
}
