import re
import json
from pathlib import Path
import os

def parse_lang_file(file_path):
    lang_dict = {}
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith('#'):
                    continue
                match = re.match(r'^([^=]+)=(.*)$', line)
                if match:
                    key = match.group(1).strip()
                    value = match.group(2).strip()
                    lang_dict[key] = value
    except FileNotFoundError:
        print(f"⚠️ Файл {file_path} не найден!")
    except Exception as e:
        print(f"❌ Ошибка при чтении {file_path}: {e}")
    return lang_dict

def read_missing_keys(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            keys = [line.strip() for line in f if line.strip()]
        result = {}
        for key in keys:
            # Определяем чистое имя
            if key.startswith('item.hbm.'):
                base = key[9:]
            elif key.startswith('block.hbm.'):
                base = key[10:]
            else:
                base = key
            # Убираем .name если есть
            if base.endswith('.name'):
                base = base[:-5]
            # Если есть точка (вложенность), берём последнюю часть
            if '.' in base:
                base = base.split('.')[-1]
            result[base] = key
        return result
    except FileNotFoundError:
        print(f"⚠️ Файл {file_path} не найден!")
        return {}
    except Exception as e:
        print(f"❌ Ошибка при чтении {file_path}: {e}")
        return {}

def generate_search_keys(clean_name):
    """
    Генерирует список возможных ключей для поиска в старом словаре
    """
    variants = []
    base = clean_name

    # Базовые варианты с префиксами и без
    prefixes = ['', 'item.', 'tile.', 'block.', 'powder.']
    suffixes = ['', '.name']

    # Если есть 'tiny', добавим перестановку
    if 'tiny' in base:
        # Например, powder_tiny_actinium -> actinium_tiny
        parts = base.split('_')
        if 'tiny' in parts:
            # Удаляем tiny из частей и добавляем в конец
            parts_without_tiny = [p for p in parts if p != 'tiny']
            if parts_without_tiny:
                reordered = '_'.join(parts_without_tiny + ['tiny'])
                # Добавляем варианты с перестановкой
                for p in prefixes:
                    for s in suffixes:
                        variants.append(f"{p}{reordered}{s}")
        # Также пытаемся заменить подчёркивания на ничего (для случаев типа actiniumtiny)
        alt = base.replace('_', '')
        if alt != base:
            for p in prefixes:
                for s in suffixes:
                    variants.append(f"{p}{alt}{s}")

    # Основные варианты
    for p in prefixes:
        for s in suffixes:
            variants.append(f"{p}{base}{s}")

    # Для bomb_caller: добавим общий ключ
    if base.startswith('bomb_caller'):
        variants.append('item.bomb_caller.name')
        variants.append('item.bomb_caller')

    # Убираем дубликаты, но сохраняем порядок
    seen = set()
    unique = []
    for v in variants:
        if v not in seen:
            seen.add(v)
            unique.append(v)
    return unique

def extract_translations(missing_keys_dict, source_dict):
    result = {}
    not_found = []
    for clean_name, full_key in missing_keys_dict.items():
        search_keys = generate_search_keys(clean_name)
        found = False
        for sk in search_keys:
            if sk in source_dict:
                result[full_key] = source_dict[sk]
                found = True
                break
        if not found:
            not_found.append(full_key)
    return result, not_found

def save_json_style(output_path, translations_dict):
    try:
        if os.path.exists(output_path):
            os.remove(output_path)
            print(f"   🗑️ Удалён старый файл: {output_path}")
        with open(output_path, 'w', encoding='utf-8') as f:
            sorted_items = sorted(translations_dict.items())
            for i, (key, value) in enumerate(sorted_items):
                escaped_value = value.replace('"', '\\"')
                comma = "," if i < len(sorted_items) - 1 else ""
                f.write(f'  "{key}": "{escaped_value}"{comma}\n')
        print(f"✅ Файл сохранён: {output_path}")
        print(f"   Всего переводов: {len(translations_dict)}")
        return True
    except Exception as e:
        print(f"❌ Ошибка при сохранении {output_path}: {e}")
        return False

def save_not_found_keys(output_path, not_found_keys, lang_name):
    if not not_found_keys:
        return
    try:
        if os.path.exists(output_path):
            os.remove(output_path)
            print(f"   🗑️ Удалён старый файл: {output_path}")
        with open(output_path, 'w', encoding='utf-8') as f:
            for key in sorted(not_found_keys):
                f.write(f"{key}\n")
        print(f"⚠️ Не найденные ключи сохранены в: {output_path}")
        print(f"   Всего: {len(not_found_keys)} ключей")
    except Exception as e:
        print(f"❌ Ошибка при сохранении {output_path}: {e}")

def main():
    print("=" * 70)
    print("🔍 ИЗВЛЕЧЕНИЕ ПЕРЕВОДОВ В СТИЛЕ JSON")
    print("=" * 70)

    # НАСТРОЙКА ПУТЕЙ
    missing_keys_file = r"missing_keys.txt"
    en_us_source = r"en_US.lang"
    ru_ru_source = r"ru_RU.lang"
    en_us_output = r"en_US_to_add.txt"
    ru_ru_output = r"ru_RU_to_add.txt"

    print("\n📖 Чтение файлов...")
    missing_keys_dict = read_missing_keys(missing_keys_file)
    if not missing_keys_dict:
        print(f"❌ Не найдено ключей в файле {missing_keys_file}")
        input("\nНажмите Enter для выхода...")
        return

    print(f"   Найдено отсутствующих ключей: {len(missing_keys_dict)}")

    sample = list(missing_keys_dict.items())[:5]
    print("\n   Примеры чистых имён:")
    for clean, full in sample:
        print(f"     {clean} -> {full}")

    print(f"\n📖 Чтение {en_us_source}...")
    en_dict = parse_lang_file(en_us_source)
    print(f"   Загружено переводов: {len(en_dict)}")

    print(f"\n📖 Чтение {ru_ru_source}...")
    ru_dict = parse_lang_file(ru_ru_source)
    print(f"   Загружено переводов: {len(ru_dict)}")

    print("\n🔍 Поиск переводов для отсутствующих ключей...")

    en_translations, en_not_found = extract_translations(missing_keys_dict, en_dict)
    print(f"\n📗 Английские переводы:")
    print(f"   Найдено: {len(en_translations)}")
    print(f"   Не найдено: {len(en_not_found)}")
    if en_translations:
        print("\n   Примеры найденных переводов:")
        sample_items = list(en_translations.items())[:3]
        for key, value in sample_items:
            print(f"     {key} -> {value}")

    ru_translations, ru_not_found = extract_translations(missing_keys_dict, ru_dict)
    print(f"\n📕 Русские переводы:")
    print(f"   Найдено: {len(ru_translations)}")
    print(f"   Не найдено: {len(ru_not_found)}")
    if ru_translations:
        print("\n   Примеры найденных переводов:")
        sample_items = list(ru_translations.items())[:3]
        for key, value in sample_items:
            print(f"     {key} -> {value}")

    print("\n💾 Сохранение файлов...")
    if en_translations:
        save_json_style(en_us_output, en_translations)
    if ru_translations:
        save_json_style(ru_ru_output, ru_translations)

    if en_not_found:
        en_missing_file = "en_US_not_found.txt"
        save_not_found_keys(en_missing_file, en_not_found, "en_US")
    if ru_not_found:
        ru_missing_file = "ru_RU_not_found.txt"
        save_not_found_keys(ru_missing_file, ru_not_found, "ru_RU")

    print("\n" + "=" * 70)
    print("📊 ИТОГОВАЯ СТАТИСТИКА:")
    print(f"   Всего отсутствующих ключей: {len(missing_keys_dict)}")
    print(f"   Найдено английских переводов: {len(en_translations)}")
    print(f"   Найдено русских переводов: {len(ru_translations)}")
    if en_translations or ru_translations:
        print(f"\n📁 Созданные файлы для вставки:")
        if en_translations:
            print(f"   - {en_us_output}")
        if ru_translations:
            print(f"   - {ru_ru_output}")
    if en_not_found or ru_not_found:
        print(f"\n⚠️ ВНИМАНИЕ: Для некоторых ключей не найдены переводы!")
        if en_not_found:
            print(f"   - en_US_not_found.txt ({len(en_not_found)} ключей)")
        if ru_not_found:
            print(f"   - ru_RU_not_found.txt ({len(ru_not_found)} ключей)")
        # Показываем примеры не найденных ключей
        print("\n   Примеры не найденных ключей:")
        if en_not_found:
            for key in sorted(en_not_found)[:5]:
                clean = key.replace('item.hbm.', '').replace('block.hbm.', '')
                print(f"     {key} (чистое имя: {clean})")

    print("\n💡 Инструкции:")
    print("   1. Откройте файлы *_to_add.txt")
    print("   2. Скопируйте ВСЕ строки из них")
    print("   3. Вставьте в конец соответствующих JSON-файлов локализации")
    print("   4. Для ключей из *_not_found.txt добавьте переводы вручную")
    print("\n" + "=" * 70)
    input("Нажмите Enter для выхода...")

if __name__ == "__main__":
    main()