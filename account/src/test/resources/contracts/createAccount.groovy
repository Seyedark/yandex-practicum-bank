import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create account"
    request {
        method POST()
        url "/account"
        headers {
            contentType(applicationJson())
        }
        body([
                login      : "login",
                password   : "12345",
                firstName  : "Тест",
                lastName   : "Тестов",
                email      : "login@mail.ru",
                "birthDate": "1990-01-01"
        ])
    }
    response {
        status OK()
    }
}