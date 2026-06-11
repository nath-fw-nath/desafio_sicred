.PHONY: run test logs clean local-test smoke negative local-clean report serve

.DEFAULT_GOAL := run

# ── Docker targets ────────────────────────────────────────────────────────────

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

# ── Local targets (requer Java 17 e Maven instalados) ─────────────────────────

local-test:
	mvn test

smoke:
	mvn test -Dgroups=smoke

negative:
	mvn test -Dgroups=negative

# Uso: make local-test-class CLASS=AuthTest
#      make local-test-class CLASS="AuthTest,ProductTest"
#      make local-test-class CLASS="ProductTest#shouldReturn404ForNonExistentProductId"
local-test-class:
	mvn test -Dtest=$(CLASS)

local-clean:
	mvn clean test

report:
	mvn allure:report

serve:
	mvn allure:serve
