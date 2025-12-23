import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should find account by login with users"
    request {
        method GET()
        url("/account/full") {
            queryParameters {
                parameter("login", "login")
            }
        }
    }
    response {
        status OK()
        body([
                login              : "login",
                password           : "12345",
                firstName          : "Тест",
                lastName           : "Тестов",
                email              : "login@mail.ru",
                "birthDate"        : "1990-01-01",
                shortAccountDtoList: [
                        [
                                login    : "login",
                                firstName: "Тест1",
                                lastName : "Тестов1",
                        ]
                ],
                accountBalanceDtoList: [
                        [
                                currency: "RUB"
                        ]
                ]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}