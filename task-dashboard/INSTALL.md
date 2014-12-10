INSTALL

1. install zookeeper http://zookeeper.apache.org/releases.html

2. Login zookeeper and add auth info: 
sudo /path/to/zookeeper/bin/zkCli.sh -server 27.0.0.1:2181
[zk: 127.0.0.1:2181(CONNECTED) 1 ]  addauth digest "parox:parox606"

3. Create following node

create /parox parox
create /parox/task task
create /parox/task/config config
create /parox/task/config/jar_upload_root /home/parox/static/task/jar
create /parox/task/config/jar_download_url http://127.0.0.1:8080/task/jar.do
create /parox/task/job job
create /parox/task/job_list job_list

4. install mysql and init sql from doc/INIT_v1.1.x.sql

5. init persist.properties reference to parent pom.xml