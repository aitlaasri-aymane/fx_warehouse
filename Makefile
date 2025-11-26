build:
	.\mvnw clean package -DskipTests

run: build
	docker-compose up --build

stop:
	docker-compose down

test:
	.\mvnw test