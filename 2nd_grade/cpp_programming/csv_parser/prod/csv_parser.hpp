#ifndef csv_parser_hpp
#define csv_parser_hpp

#include <iostream>
#include <fstream>
#include <tuple>
#include <sstream>
#include <string>
#include <vector>
#include <stdexcept>


class CSVParseException : public std::runtime_error {
private:
    size_t row_;
    size_t col_;
    
public:
    CSVParseException(const std::string& message, size_t row, size_t col)
        : std::runtime_error(message), row_(row), col_(col) {}

    size_t row() const { return row_; }
    size_t col() const { return col_; }
};


template<typename T>
T parse_element(const std::string& field, size_t row, size_t col) {
    std::stringstream ss(field);
    T value;
    ss >> value;

    if (ss.fail()) {
        throw CSVParseException("Element conversion error: \"" + field + "\"", row, col);
    }

    return value;
}


template<>
inline std::string parse_element<std::string>(const std::string& field, size_t row, size_t col) {
    if (field.empty()) {
        throw CSVParseException("Error: empty field", row, col);
    }
    return field;
}


template<typename... Args>
class CSVParser {
    using TupleType = std::tuple<Args...>;

private:
    std::istream& file_;
    char column_delimiter_;
    char row_delimiter_;
    char quote_char_;
    int skip_lines_;
    size_t current_row_;
    
    std::vector<std::string> split_line(const std::string& line);

public:
    CSVParser(std::istream& file, int skip_lines,
              char column_delimiter = ',', char row_delimiter = '\n', char quote_char = '"');

    class CSVIterator;

    CSVIterator begin();
    CSVIterator end();
};


template<typename... Args>
CSVParser<Args...>::CSVParser(std::istream& file, int skip_lines, char column_delimiter, char row_delimiter, char quote_char)
    : file_(file), column_delimiter_(column_delimiter), row_delimiter_(row_delimiter),
      quote_char_(quote_char), skip_lines_(skip_lines), current_row_(0) {

    for (int i = 0; i < skip_lines_; ++i) {
        std::string dummy;
        std::getline(file_, dummy);
    }
}


template<typename... Args>
std::vector<std::string> CSVParser<Args...>::split_line(const std::string& line) {
    std::vector<std::string> result;
    std::string current_field;
    bool inside_quotes = false;
    size_t col_number = 0;

    for (size_t i = 0; i < line.size(); ++i) {
        char c = line[i];

        if (c == quote_char_) {
            if (inside_quotes && i + 1 < line.size() && line[i + 1] == quote_char_) {
                current_field += quote_char_;
                ++i;
            }
            else
                inside_quotes = !inside_quotes;
        }
        else if (c == column_delimiter_ && !inside_quotes) {
            result.push_back(current_field);
            current_field.clear();
            col_number++;
        }
        else
            current_field += c;
    }

    if (inside_quotes) {
        throw CSVParseException("Error: unclosed quotation mark", current_row_, col_number);
    }

    result.push_back(current_field);
    return result;
}


template<typename... Args>
class CSVParser<Args...>::CSVIterator {
    using TupleType = std::tuple<Args...>;

private:
    std::istream* file_;
    TupleType current_;
    bool end_;
    CSVParser<Args...>* parser_;

public:
    CSVIterator(std::istream* file = nullptr, bool end = false, CSVParser* parser = nullptr);

    CSVIterator& operator++();
    const TupleType& operator*() const;
    bool operator!=(const CSVIterator& other) const;
};


template<typename... Args>
CSVParser<Args...>::CSVIterator::CSVIterator(std::istream* file, bool end, CSVParser* parser)
    : file_(file), end_(end), parser_(parser) {
    if (file_ && !end_) {
        operator++();
    }
}


template<std::size_t Index, typename Tuple>
void parse_tuple(const std::vector<std::string>& fields, Tuple& t, size_t row) {
    if constexpr (Index < std::tuple_size<Tuple>::value) {
        using ElementType = std::tuple_element_t<Index, Tuple>;
        std::get<Index>(t) = parse_element<ElementType>(fields[Index], row, Index + 1);
        parse_tuple<Index + 1>(fields, t, row);
    }
}



template<typename... Args>
typename CSVParser<Args...>::CSVIterator& CSVParser<Args...>::CSVIterator::operator++() {
    if (!file_ || file_->eof()) {
        end_ = true;
        return *this;
    }

    std::string line;
    if (!std::getline(*file_, line, parser_->row_delimiter_)) {
        end_ = true;
        return *this;
    }

    parser_->current_row_++;

    std::vector<std::string> fields = parser_->split_line(line);

    if (fields.size() != sizeof...(Args)) {
        throw CSVParseException("Error: incorrect number of fields", parser_->current_row_, fields.size());
    }

    parse_tuple<0>(fields, current_, parser_->current_row_);
    return *this;
}


template<typename... Args>
const typename CSVParser<Args...>::TupleType& CSVParser<Args...>::CSVIterator::operator*() const {
    return current_;
}


template<typename... Args>
bool CSVParser<Args...>::CSVIterator::operator!=(const CSVIterator& other) const {
    return end_ != other.end_;
}

template<typename... Args>
typename CSVParser<Args...>::CSVIterator CSVParser<Args...>::begin() {
    return CSVIterator(&file_, false, this);
}

template<typename... Args>
typename CSVParser<Args...>::CSVIterator CSVParser<Args...>::end() {
    return CSVIterator(nullptr, true, this);
}


#endif /* csv_parser_hpp */
