var exec = require('cordova/exec');

exports.PrintImage = function (PrinterMac, ImageUrl, success, error) {
    exec(success, error, 'PrintImage', 'PrintImage', [PrinterMac, ImageUrl ]);
};

exports.SendCommandToPrinter = function (PrinterMac, CommandText, success, error) {
    exec(success, error, 'success, error, 'PrintImage', 'SendCommandToPrinter', [PrinterMac, ImageUrl ]', 'SendCommandToPrinter', [PrinterMac, CommandText ]);
};

exports.GetPrinterLanguage = function (PrinterMac, success, error) {
    exec(success, error, 'success, error, 'PrintImage', 'GetPrinterLanguage', [PrinterMac, ImageUrl ]', 'GetPrinterLanguage', [PrinterMac ]);
};
