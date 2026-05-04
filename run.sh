#!/bin/bash

echo "====================================================="
echo "   KIEM TRA MA NGUON TRUOC KHI PUSH LEN GITHUB"
echo "====================================================="

# Chạy lệnh Maven giống y hệt file ci.yml trên GitHub
mvn clean package

echo ""
echo "====================================================="
echo " NEU HIEN 'BUILD SUCCESS' -> CODE DA CHUAN!"
echo " Bay gio ban hay dung Git de push len GitHub:"
echo " 1. git add ."
echo " 2. git commit -m 'Fix loi pipeline'"
echo " 3. git push"
echo "====================================================="