HOST=http://localhost:6969
ENDPOINT=/api/product

run:
	mvn spring-boot:run

post_smartphone:
	curl -X POST $(HOST)$(ENDPOINT) \
	-H "Content-Type: application/json" \
	-d "{\"name\": \"Smartphone\", \"description\": \"A sleek smartphone with excellent features\", \"price\": 799.99}"

post_laptop:
	curl -X POST $(HOST)$(ENDPOINT) \
	-H "Content-Type: application/json" \
	-d "{\"name\": \"Laptop\", \"description\": \"A dell laptop with all the latest features\", \"price\": 999.99}" | jq

get_products:
	curl -X GET $(HOST)$(ENDPOINT) | jq

delete_products:
	curl -X DELETE $(HOST)$(ENDPOINT)

test: post_smartphone get_products




docker_run_mongo:
	docker run -d -p 27017:27017 --name mongodb mongo:6.0

docker_stop_mongo:
	docker stop mongodb
