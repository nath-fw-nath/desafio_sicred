.PHONY: run test clean logs
.DEFAULT_GOAL := run

run:
	docker-compose up --build -d
	docker wait desafio-qe-tests
	@until curl -sf http://localhost:5050 > /dev/null 2>&1; do printf '.'; sleep 1; done
	@echo ""
	@open http://localhost:5050 2>/dev/null || xdg-open http://localhost:5050 2>/dev/null || echo "Acesse: http://localhost:5050"
	@echo ""
	@docker-compose logs tests | grep -E "(Tests run|FAILURE|SUCCESS|ERROR|BUILD)" | tail -15

test:
	docker-compose run --rm tests

logs:
	docker-compose logs -f tests

clean:
	docker-compose down -v
