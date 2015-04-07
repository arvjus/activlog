## ActivLog
Activity Logger is an Android application for logging sport activity. ![icon](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/small-icon.png)


### License

The MIT License (MIT)

Copyright © 2012 Arvid Juskaitis <arvydas.juskaitis@gmail.com>


### Some of screenshots

Click to view.

[![Logging](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/1-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/1.png)  [![Statistics](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/2-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/2.png)  [![Activities](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/3-thumb.png)](https://raw.githubusercontent.com/arvjus/activlog/master/androidmarket/3.png)


### Intro

Activity Logger is an application for logging sport activity. There are two version of application
* Activity Logger Lite - free version of application with limited functionality.
* Activity Logger - full-featured application.

By using application, user will be able to:
* Log activity by using predefined activity template. (e.g. Running; distance: 5km, duration: 30min)
* Define a template for activity with custom-defined attributes.
* Display statistics for given period of time.

There is a widget provided for quick application access. This can speed-up reporting by entering values directly in 
a widget, thus eliminating need to launch application.

In additional to basic functionality, commercial version of application adds:
* Auto-tracking of distance and time duration.
* Scheduled reminder
* Activity categories
* Ability to create custom reports for statistics 
* Export/import/maintain collected data.


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
