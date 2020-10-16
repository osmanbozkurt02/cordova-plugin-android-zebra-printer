var exec = require('cordova/exec');

exports.PrintImage = function (PrinterMac, ImageUrl, success, error) {
    exec(success, error, 'ZebraPrinterAndroid', 'PrintImage', [PrinterMac, ImageUrl ]);
};

exports.SendCommandToPrinter = function (PrinterMac, CommandText, success, error) {
    exec(success, error, 'ZebraPrinterAndroid', 'SendCommandToPrinter', [PrinterMac, CommandText ]);
};

exports.GetPrinterLanguage = function (PrinterMac, success, error) {
    exec(success, error, 'ZebraPrinterAndroid', 'GetPrinterLanguage', [PrinterMac ]);
};

 