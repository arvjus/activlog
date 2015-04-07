## ActivLog

![icon](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/small-icon.png)

Activity Logger is an Android application for logging sport activity. 

### License

The MIT License (MIT)

Copyright © 2012 Arvid Juskaitis <arvydas.juskaitis@gmail.com>


### Introduction

Activity Logger is an Android application for logging sport activity. 

* Log activity by using predefined activity template. (e.g. Running; distance: 5km, duration: 30min)
* Define a template for activity with custom-defined attributes.
* Display statistics for given period of time.
* Export/import/maintain collected data.


### Some of screenshots

Click to view.

[![Logging](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/1-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/1.png)  [![Statistics](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/2-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/2.png)  [![Activities](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/3-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/3.png)


### Database schema (SQLite)

Table categories

Column name|type
---|---
category_id|int
name|text


Table activities

Column name|type
---|---
activity_id				|int
category_id				|int
name					|text
enabled					|boolean


Table activity_attributes

Column name|type
---|---
activity_attribute_id	|int
activity_id				|int
type					|int		/* DISTANCE, DURATION, TIMES, CUSTOM */
name					|text
default_value			|text
enabled					|boolean


Table activity_logs

Column name|type
---|---
activity_log_id			|int
activity_id				|int
create_date				|text


Table activity_attribute_logs

Column name|type
---|---
activity_attribute_log_id	|int
activity_log_id				|int
activity_attribute_id		|int
activity_id					|int
value						|text
