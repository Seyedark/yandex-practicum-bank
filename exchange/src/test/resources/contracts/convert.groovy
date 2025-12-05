package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should convert"
    request {
        method POST()
        url "/exchange/convert"
        headers {
            contentType(applicationJson())
        }
        body([
                convertAmount: 1,
                currencyFrom : "USD",
                currencyTo   : "USD"
        ])
    }
    response {
        body([
                convertedAmount: 1
        ])
        status OK()
    }
}