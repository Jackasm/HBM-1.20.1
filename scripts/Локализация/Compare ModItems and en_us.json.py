import re
import json
from pathlib import Path

# Классы, которые наследуют BlockItem и используются в ModItems.java
BLOCK_CLASSES = {
    "BlockItem", "SimpleOBJItem", "BarrelItem", "MachinePressItem",
    "FurnaceSteelItem", "FurnaceIronItem", "FurnaceCombinationItem",
    "SawmillItem", "BoilerItem", "AutosawItem", "AshpitItem",
    "HeaterFireboxItem", "HeaterOvenItem", "HeaterOilburnerItem",
    "ItemRotaryFurnace", "ItemMachineCrucible", "ItemSolderingStation",
    "ItemArcWelder", "ItemFluidDuct", "ItemFluidDuctBox",
    "ItemFoundryMold", "ItemFoundryBasin", "ItemFoundryChannel",
    "ItemFoundryOutlet", "TurretSentryItem", "TurretSentryDamagedItem",
    "SolarMirrorItem", "SolarBoilerItem", "CapacitorItem", "TeslaItem",
    "ItemLaunchPad", "ItemDetCord", "LandmineItem", "ItemN2",
    "ItemBlockNukeCustom", "ItemBlockCoke", "ItemBlockConcreteColored",
    "ItemBlockConcreteColoredExt", "ItemBlockOreBasalt",
    "DecoBlockItem", "DecoBlockAltItem", "ItemSnowglobe", "ItemPlushie",
    "MachineFluidTankItem", "FilingCabinetItem", "SteelGrateItem",
    "SteelScaffoldItem", "ItemBlockResourceStone",
    "ItemModBlock", "ItemBlockCoke",
}

# Вкладки, которые указывают на блоки
BLOCK_TABS = {
    "BLOCK_TAB", "MACHINE_TAB"
}

def extract_item_and_block_keys(java_content):
    """
    Извлекает все имена зарегистрированных предметов и блоков из содержимого ModItems.java
    и возвращает множество сгенерированных ключей локализации.
    """
    keys = set()

    # Ищем все вызовы register
    pattern = re.compile(r'register\s*\(\s*([^,)]*?)\s*,\s*["\']([^"\']+)["\']\s*,\s*\(\)\s*->\s*([^,}]+)')

    for match in pattern.finditer(java_content):
        tab_part = match.group(1).strip()
        name = match.group(2)
        supplier = match.group(3)

        # Определяем, является ли это блоком
        is_block = False

        # Проверяем по вкладке
        if any(tab in tab_part for tab in BLOCK_TABS):
            is_block = True

        # Проверяем по классу в Supplier
        if not is_block:
            for cls in BLOCK_CLASSES:
                if cls in supplier:
                    is_block = True
                    break

        # Формируем ключ
        key_name = name.replace('/', '.')
        if is_block:
            full_key = f"block.hbm.{key_name}"
        else:
            full_key = f"item.hbm.{key_name}"

        keys.add(full_key)

    return keys

def load_localization_keys(json_content):
    """Загружает ключи из содержимого JSON-файла локализации."""
    try:
        data = json.loads(json_content)
        return set(data.keys())
    except json.JSONDecodeError:
        # Пробуем найти начало и конец JSON-объекта
        start = json_content.find('{')
        end = json_content.rfind('}')
        if start != -1 and end != -1:
            json_str = json_content[start:end+1]
            try:
                data = json.loads(json_str)
                return set(data.keys())
            except:
                pass
        return set()

def main():
    """Главная функция для запуска из IDE."""

    print("=" * 60)
    print("🔍 ПОИСК ОТСУТСТВУЮЩИХ КЛЮЧЕЙ ЛОКАЛИЗАЦИИ")
    print("=" * 60)

    # НАСТРОЙКА ПУТЕЙ - ИЗМЕНИТЕ ЭТИ ПЕРЕМЕННЫЕ ПОД СВОИ ФАЙЛЫ!
    java_file_path = r"ModItems.java"  # Путь к вашему ModItems.java
    lang_file_path = r"en_us.json"     # Путь к вашему файлу локализации
    output_file_path = r"missing_keys.txt"  # Куда сохранить результат
    output_as_json = False  # True - сохранить как JSON, False - как список

    print(f"\n📁 Java-файл: {java_file_path}")
    print(f"📁 Файл локализации: {lang_file_path}")
    print(f"📁 Выходной файл: {output_file_path}")

    # Проверяем существование файлов
    java_path = Path(java_file_path)
    lang_path = Path(lang_file_path)

    if not java_path.exists():
        print(f"\n❌ Ошибка: файл {java_path} не найден!")
        print("   Укажите правильный путь в переменной java_file_path")
        input("\nНажмите Enter для выхода...")
        return

    if not lang_path.exists():
        print(f"\n❌ Ошибка: файл {lang_path} не найден!")
        print("   Укажите правильный путь в переменной lang_file_path")
        input("\nНажмите Enter для выхода...")
        return

    # Читаем файлы
    print("\n📖 Чтение файлов...")
    with open(java_path, 'r', encoding='utf-8') as f:
        java_content = f.read()

    with open(lang_path, 'r', encoding='utf-8') as f:
        lang_content = f.read()

    # Извлекаем ключи
    print("🔍 Извлечение ключей из ModItems.java...")
    java_keys = extract_item_and_block_keys(java_content)
    print(f"   Найдено {len(java_keys)} ключей")

    print("🔍 Загрузка ключей из локализации...")
    lang_keys = load_localization_keys(lang_content)
    print(f"   Найдено {len(lang_keys)} ключей")

    # Находим отсутствующие
    missing = java_keys - lang_keys
    print(f"\n📊 Отсутствует в локализации: {len(missing)} ключей")

    if missing:
        # Показываем первые 10 отсутствующих ключей
        print("\n❌ Примеры отсутствующих ключей:")
        for key in sorted(missing)[:10]:
            print(f"   - {key}")
        if len(missing) > 10:
            print(f"   ... и ещё {len(missing) - 10} ключей")

        # Сохраняем результат
        output_path = Path(output_file_path)
        if output_as_json:
            result = {k: "" for k in sorted(missing)}
            with open(output_path, 'w', encoding='utf-8') as f:
                json.dump(result, f, indent=2, ensure_ascii=False)
            print(f"\n✅ Результат сохранён в {output_path} (JSON-формат)")
        else:
            with open(output_path, 'w', encoding='utf-8') as f:
                for key in sorted(missing):
                    f.write(key + "\n")
            print(f"\n✅ Результат сохранён в {output_path} (построчный список)")

        print("\n💡 Теперь вы можете:")
        print("   1. Открыть файл с результатами")
        print("   2. Скопировать ключи в ваш JSON-файл локализации")
        print("   3. Добавить переводы для каждого ключа")
    else:
        print("\n✅ Отлично! Все ключи присутствуют в локализации!")

    print("\n" + "=" * 60)
    input("Нажмите Enter для выхода...")

if __name__ == "__main__":
    main()