# Example MDS Order JSON Message

    {
      "type": "order",
      "requiredChanges": {
        "bookingAmendments": [
          {
            "bookingHref": "http://easyjet-dot-paxportcloud.appspot.com/v1/easyjet/bookings/ER8VMHN",
            "flightSeatAmendments": [
              {
                "amendmentType": "UPDATE",
                "flightSegmentIdentifier": "eyJkcGMiOiJCUlMiLCJkZHQiOiIyMDE3LTA1LTEyVDE5OjU1IiwiYXBjIjoiQUxDIiwib2MiOiJFWlkiLCJmbiI6IjYwNzUifQ==",
                "travellerIdentifier": "201E94D1",
                "allocatedSeatNumber": "6C"
              }
            ]
          }
        ]
      },
      "price": {
        "amount": "3.56",
        "currency": "GBP",
        "constituentItems": [
          {
            "description": "Seat Allocation",
            "amountPerUnit": "3.00",
            "currency": "GBP",
            "quantity": 1,
            "pricingCode": "FlightSeatAllocation"
          },
          {
            "description": "Credit Card Charge",
            "amountPerUnit": "0.06",
            "currency": "GBP",
            "quantity": 1,
            "pricingCode": "Fees"
          },
          {
            "description": "Provider Fee",
            "amountPerUnit": "0.50",
            "currency": "GBP",
            "quantity": 1
          }
        ],
        "paymentOptions": [
          {
            "paymentType": "DEBIT_CARD",
            "price": {
              "amount": "0.00",
              "currency": "GBP"
            }
          },
          {
            "paymentType": "CREDIT_CARD",
            "price": {
              "amount": "0.06",
              "currency": "GBP"
            }
          }
        ]
      },
      "payments": [
        {
          "amount": "3.06",
          "currency": "GBP",
          "paymentType": "CREDIT_CARD",
          "payor": "Joe Bloggs",
          "payee": "EZY",
          "last4CardDigits": "1111"
        }
      ],
      "supplierTransactions": [
        {
          "transactionIdentity": "BgQ0Yz3X51y6",
          "type": "BOOKING_AMENDMENT",
          "status": "SUCCEEDED",
          "mostRelevantDate": "2017-05-12",
          "totalCost": {
            "amount": "3.06",
            "currency": "GBP"
          },
          "items": [
            {
              "itemIdentity": "grA7W8JJ0BG4",
              "relevantDate": "2017-05-12",
              "service": "FlightSeatAllocation",
              "description": "Seat Allocation",
              "baseUnitCost": {
                "amount": "3.00",
                "currency": "GBP"
              },
              "unitCostWithFees": {
                "amount": "3.06",
                "currency": "GBP"
              },
              "unitPrice": {
                "amount": "3.56",
                "currency": "GBP"
              }
            }
          ],
          "updatedAt": "2016-11-14T14:26:56.671Z",
          "totalPrice": {
            "amount": "3.56",
            "currency": "GBP"
          },
          "supplier": "EZY",
          "system": "easyJet b2b",
          "bookingReference": "ER8VMHN",
          "paxName": "DVJBVUBYBLGYX",
          "createdAt": "2016-11-14T14:26:54Z"
        }
      ],
      "status": "SUCCEEDED",
      "createdAt": "2016-11-14T14:26:54Z",
      "updatedAt": "2016-11-14T14:26:56Z",
      "trackingInfo": {
        "target": "TEST",
        "securityToken": {
          "value": "NULL_TOKEN"
        },
        "explicitTarget": "TEST",
        "explicitSecurityToken": {
          "value": "NULL_TOKEN"
        },
        "internalTracingId": "BjbYR2A630NJQb0JG8ZPVno5",
        "requestId": "dxZAGEMBgQVkMvKRYVo8wa2J"
      },
      "identifier": "BgQ0Yz3X51y6",
      "relationships": [
        {
          "href": "http://easyjet-dot-paxportcloud.appspot.com/v1/easyjet/bookings/ER8VMHN",
          "rel": "ex:fetch-booking"
        }
      ],
      "_links": {
        "self": {
          "href": "http://amendments-dot-paxportcloud.appspot.com/v1/retail/orders/BgQ0Yz3X51y6"
        }
      }
    }