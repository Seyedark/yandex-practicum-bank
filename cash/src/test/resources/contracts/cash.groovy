package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should change cash"
    request {
        method PATCH()
        url "/cash"
        headers {
            contentType(applicationJson())
        }
        body([
                login       : "login",
                currency    : "RUB",
                changeAmount: 1,
                actionEnum  : "ACCRUAL"
        ])
    }
    response {
        status OK()
    }
}