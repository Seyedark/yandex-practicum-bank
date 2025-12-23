package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should change cash"
    request {
        method PATCH()
        url "/transfer/transfer"
        headers {
            contentType(applicationJson())
        }
        body([
                login       : "login",
                changeAmount: 1,
                actionEnum  : "ACCRUAL",
                currencyFrom: "RUB",
                currencyTo  : "RUB"
        ])
    }
    response {
        status OK()
    }
}