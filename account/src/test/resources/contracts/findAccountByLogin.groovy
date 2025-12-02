package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should find account by login"
    request {
        method GET()
        url("/account") {
            queryParameters {
                parameter("login", "login")
            }
        }
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body([
                login      : "login",
                password   : "12345",
                firstName  : "Тест",
                lastName   : "Тестов",
                email      : "login@mail.ru",
                birthDate: "1990-01-01"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}