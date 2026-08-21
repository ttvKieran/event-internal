#!/bin/bash

# sh performance/scripts/run-test.sh [tên_file_test] [mức_tải]

TEST_SCRIPT=${1:-"registration"}
PROFILE=${2:-"spike"}

echo "Bắt đầu chạy test: ${TEST_SCRIPT}.js với cấu hình tải: ${PROFILE}"

# Thư mục chứa kết qủa
mkdir -p performance/results

docker run --rm -i \
  --user $(id -u):$(id -g) \
  -v $(pwd)/performance:/performance \
  --network host \
  -e PROFILE="${PROFILE}" \
  -e BASE_URL="http://localhost:8083/api/v1" \
  grafana/k6:latest run /performance/tests/${TEST_SCRIPT}.js \
  --out json=/performance/results/summary.json

echo "Test hoàn tất! Kết quả (JSON raw) được lưu tại: performance/results/summary.json"
